package com.riddlesvillage.core.database;

import com.google.common.collect.Lists;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.*;
import com.mongodb.connection.ClusterSettings;
import com.riddlesvillage.core.CoreException;
import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.database.data.Credentials;
import org.bson.Document;

import java.io.Closeable;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public final class Database implements Closeable {

    private static Database INSTANCE = new Database();

    public static MongoClient client;
    public static MongoDatabase database = null;
    private static MongoCollection<Document> playerData;

    private Database() {}

    public void init(Credentials credentials) throws CoreException {
        if (client != null) throw new CoreException("Database connection already established");

        RiddlesCore.log("Database connection pool is being created...");
        client = MongoClients.create(MongoClientSettings.builder()
                .clusterSettings(ClusterSettings.builder()
                                .hosts(Collections.singletonList(new ServerAddress(credentials.getAddress())))
                                .build()
                ).credentialList(Collections.singletonList(MongoCredential.createCredential(
                        credentials.getUser(),
                        credentials.getDatabase(),
                        credentials.getPass().toCharArray()
                ))).build());


        database = client.getDatabase("riddlesvillage");
        playerData = database.getCollection("player_data");

        RiddlesCore.log("Database connected successfully to %s", credentials.getAddress());

        // Create Async Mongo Access Threads
        List<MongoAccessThread> accessThreads = Lists.newArrayList();

        int count = Runtime.getRuntime().availableProcessors();

        // Keep threads open
        IntStream.range(0, count - 1).forEach(c -> accessThreads.add(new MongoAccessThread()));
        accessThreads.forEach(Thread::start);

        RiddlesCore.log("%s Mongo access threads started", count);
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