package com.riddlesvillage.core.database;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.riddlesvillage.core.database.data.MongoAccessThread;
import org.bson.Document;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class DatabaseInstance {

    private static DatabaseInstance instance = null;

    public static DatabaseInstance getInstance() {
        if (instance == null) {
            instance = new DatabaseInstance();
        }
        return instance;
    }

    private static Logger log = Logger.getLogger("Database");

    public static MongoClient mongoClient = null;
    public static MongoClientURI mongoClientURI = null;
    public static MongoDatabase database = null;

    public static List<MongoAccessThread> accessThreads;

    public static MongoCollection<Document> playerData;
    protected boolean cacheData = true;

    public void startInitialization(boolean cacheData, String uri) {
        this.cacheData = cacheData;
        mongoClientURI = new MongoClientURI(uri);

        log.info("MysticalWars Database connection pool is being created...");
        mongoClient = new MongoClient(mongoClientURI);

        database = mongoClient.getDatabase("starforcemc");
     playerData = database.getCollection("player_data");

       log.info("MysticalWars Database has connected successfully!");

        createMongoAccessThreads();
    }


    private static void createMongoAccessThreads() {
        accessThreads = Lists.newArrayList();

        int count =  Runtime.getRuntime().availableProcessors();
        System.out.println("JVM returns " + count + " processors!");

        // Keep a thread open
        IntStream.range(0, count - 1).forEach(c -> accessThreads.add(new MongoAccessThread()));
        accessThreads.forEach(Thread::start);


        log.info("MysticalWars Database mongo access threads ... STARTED ...");
    }

    protected boolean isCacheData() {
        return cacheData;
    }
}