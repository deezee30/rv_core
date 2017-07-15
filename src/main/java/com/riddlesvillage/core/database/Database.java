package com.riddlesvillage.core.database;

import com.google.common.collect.Lists;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.*;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ServerSettings;
import com.riddlesvillage.core.Logger;
import com.riddlesvillage.core.database.data.Credentials;
import com.riddlesvillage.core.database.data.codec.CoreItemStackCodec;
import com.riddlesvillage.core.database.data.codec.RankCodec;
import com.riddlesvillage.core.database.data.codec.Vector3DCodec;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.io.Closeable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public final class Database implements Closeable {

    private static final String MAIN_DATABASE = "riddlesvillage";
    private static final String MAIN_PLAYER_COLLECTION = "player_data";
    private static final Database INSTANCE = new Database();

    /**
     * @deprecated Use {@link #getClient()}
     */
    public static MongoClient client;
    /**
     * @deprecated Use {@link #getDefaultDatabase()}
     */
    public static MongoDatabase database;

    private static final Codec<?>[] CODECS = new Codec[] {
            new CoreItemStackCodec(),
            new RankCodec(),
            new Vector3DCodec()
    };

    private static final CodecRegistry REGISTRY = CodecRegistries.fromRegistries(
            MongoClients.getDefaultCodecRegistry(),
            CodecRegistries.fromCodecs(CODECS)
    );

    private MongoCollection<Document> playerData;
    private boolean connected = false;

    public void init(final Logger logger,
                     final Credentials credentials) throws DatabaseException {
        Validate.notNull(logger);
        Validate.notNull(credentials);

        if (client != null) throw new DatabaseException("Database connection already established");

        logger.log("Attempting to connect to Mongo at &e%s", credentials.getAddress());

        client = MongoClients.create(MongoClientSettings.builder()
                .codecRegistry(REGISTRY)
                .clusterSettings(ClusterSettings.builder()
                        .hosts(Collections.singletonList(new ServerAddress(credentials.getAddress())))
                        .mode(ClusterConnectionMode.SINGLE)
                        .serverSelectionTimeout(3, TimeUnit.SECONDS)
                        .description("RiddlesVillage general database")
                        .build()
                ).serverSettings(ServerSettings.builder()
                        .addServerListener(new DatabaseConnectionListener(logger))
                        .build()
                ).credentialList(Collections.singletonList(MongoCredential.createCredential(
                        credentials.getUser(),
                        credentials.getDatabase(),
                        credentials.getPass().toCharArray()
                ))).build()
        );

        database = client.getDatabase(MAIN_DATABASE);
        playerData = database.getCollection(MAIN_PLAYER_COLLECTION);

        // create async Mongo access threads
        List<MongoAccessThread> accessThreads = Lists.newArrayList();

        int count = Runtime.getRuntime().availableProcessors();

        // keep threads open
        IntStream.range(0, count - 1).forEach(c -> accessThreads.add(new MongoAccessThread()));
        accessThreads.forEach(Thread::start);

        logger.log("&e%s&7 Mongo access threads started", count);
    }

    public boolean isConnected() {
        return connected;
    }

    void setConnected(final boolean connected) {
        this.connected = connected;
    }

    public static MongoClient getClient() {
        return INSTANCE.client;
    }

    public static MongoDatabase getDefaultDatabase() {
        return INSTANCE.database;
    }

    public static MongoCollection<Document> getMainPlayerCollection() {
        return INSTANCE.playerData;
    }

    public static MongoCollection<Document> getCollection(final String name) {
        return INSTANCE.database.getCollection(Validate.notNull(name));
    }

    @Override
    public void close() {
        client.close();
        client = null;
    }

    /**
     * @deprecated Use {@link #get()}
     */
    public static Database getInstance() {
        return INSTANCE;
    }

    public static Database get() {
        return INSTANCE;
    }

    /**
     * @deprecated Use {@link #getMainPlayerCollection()}
     */
    public static MongoCollection<Document> getMainCollection() {
        return INSTANCE.playerData;
    }
}