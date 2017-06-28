package com.riddlesvillage.core.database;

import com.google.common.collect.Lists;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.*;
import com.mongodb.connection.ClusterSettings;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.CoreException;
import com.riddlesvillage.core.database.data.Credentials;
import com.riddlesvillage.core.database.data.RankCodec;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.io.Closeable;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public final class Database implements Closeable {

    public static MongoClient client;
    public static MongoDatabase database = null;

    private static MongoCollection<Document> playerData;
    private static Database INSTANCE = new Database();

    private static final Codec<?>[] CODECS = new Codec[] {
            new RankCodec()
    };

    private static final CodecRegistry REGISTRY = CodecRegistries.fromRegistries(
            MongoClients.getDefaultCodecRegistry(),
            CodecRegistries.fromCodecs(CODECS)
    );

    private Database() {}

    public void init(final Credentials credentials) throws CoreException {
        Validate.notNull(credentials);
        if (client != null) throw new CoreException("Database connection already established");

        Core.log("Database connection pool is being created...");

        client = MongoClients.create(MongoClientSettings.builder()
                .codecRegistry(REGISTRY)
                .clusterSettings(ClusterSettings.builder()
                                .hosts(Collections.singletonList(new ServerAddress(credentials.getAddress())))
                                .build()
                ).build());

        database = client.getDatabase("riddlesvillage");
        playerData = database.getCollection("player_data");

        Core.log("Database connected successfully to %s", credentials.getAddress());

        // Create Async Mongo Access Threads
        List<MongoAccessThread> accessThreads = Lists.newArrayList();

        int count = Runtime.getRuntime().availableProcessors();

        // Keep threads open
        IntStream.range(0, count - 1).forEach(c -> accessThreads.add(new MongoAccessThread()));
        accessThreads.forEach(Thread::start);

        Core.log("%s Mongo access threads started", count);
    }

    @Override
    public void close() {
        client.close();
        client = null;
        // TODO: Perform closing and security checks
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public static MongoCollection<Document> getMainCollection() {
        return playerData;
    }
}