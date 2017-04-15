package com.riddlesvillage.core.database;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.result.UpdateResult;
import com.riddlesvillage.core.database.data.EnumData;
import com.riddlesvillage.core.database.data.EnumOperators;
import com.riddlesvillage.core.database.data.MongoAccessThread;
import com.riddlesvillage.core.database.query.BulkWriteQuery;
import com.riddlesvillage.core.database.query.DocumentSearchQuery;
import com.riddlesvillage.core.database.query.SingleUpdateQuery;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;


@SuppressWarnings("unchecked")
public class DatabaseAPI {

    private static DatabaseAPI instance = null;

    public volatile Map<UUID, Document> PLAYERS = new ConcurrentHashMap<>();

    public volatile Map<UUID, Long> PLAYERS_LOGINS = new ConcurrentHashMap<>();

    private volatile Map<String, String> CACHED_UUIDS = new ConcurrentHashMap<>();

    private Logger log = Logger.getLogger("DatabaseAPI");
    private final ExecutorService SERVER_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("MONGODB Server Collection Thread").build());

    public static DatabaseAPI getInstance() {
        if (instance == null) {
            instance = new DatabaseAPI();
        }
        return instance;
    }

    public void startInitialization() {
//        Document doc = DatabaseInstance.shardData.find(Filters.eq("shard", shard)).first();
//        if (doc == null) {
//            createNewShardCollection(shard);
//        }
    }

    /**
     * @param operations      Bulk write operations
     * @param async           Run Async?
     * @param doAfterOptional an optional parameter allowing you to specify extra actions after the update query is
     *                        completed. doAfterOptional is executed async or sync based on the previous async parameter.
     */
    public void bulkUpdate(List<UpdateOneModel<Document>> operations, boolean async, Consumer<BulkWriteResult> doAfterOptional) {
        if (async) {
            MongoAccessThread.submitQuery(new BulkWriteQuery<>(DatabaseInstance.playerData, operations, doAfterOptional));
        } else {
            BulkWriteResult result = DatabaseInstance.playerData.bulkWrite(operations);

            if (doAfterOptional != null)
                doAfterOptional.accept(result);
        }
    }
    /*
    if (document == null) return null;
        String[] key = data.split("\\.");
        int i =0;
        for (String s : key) {
            System.out.println(i + ": " + s + " : " + key[i]);
            i++;
        }
        Document rootDoc = (Document) document.get(key[0]);
        if (rootDoc == null) return null;
        System.out.println(rootDoc);
        Document dataObj = (Document) rootDoc.get(key[1]);
        if (dataObj == null) return null;
        System.out.println(dataObj);
        Object obj = dataObj.get(key[2]);
        System.out.println(obj);
        Class<?> clazz = obj.getClass();
     */
    public void update(UUID uuid, EnumOperators EO, String variable, Object object, boolean async, boolean updateDatabase, Consumer<UpdateResult> doAfterOptional) {
        if (PLAYERS.containsKey(uuid)) { // update local data
            Document localDoc = PLAYERS.get(uuid);
            String[] key = variable.split("\\.");
            int i=0;
            for (String k : key) {
                System.out.print(i + " "+k);
                i++;
            }
            Document rootDoc = (Document) localDoc.get(key[0]);
            Document data1 = (Document) rootDoc.get(key[1]);
            Object data = data1.get(key[2]);
            switch (EO) {
                case $SET:
                    data1.put(key[2], object);
                    rootDoc.put(key[1], data1);
                    break;
                case $INC:
                    if (data instanceof Integer)
                        rootDoc.put(key[1], ((Integer) object) + ((Integer) data));
                    else if (data instanceof Double)
                        rootDoc.put(key[1], ((Double) object) + ((Double) data));
                    else if (data instanceof Float)
                        rootDoc.put(key[1], ((Float) object) + ((Float) data));
                    else if (data instanceof Long)
                        rootDoc.put(key[1], ((Long) object) + ((Long) data));
                    break;
                case $MUL:
                    if (data instanceof Integer)
                        rootDoc.put(key[1], ((Integer) object) * ((Integer) data));
                    else if (data instanceof Double)
                        rootDoc.put(key[1], ((Double) object) * ((Double) data));
                    else if (data instanceof Float)
                        rootDoc.put(key[1], ((Float) object) * ((Float) data));
                    else if (data instanceof Long)
                        rootDoc.put(key[1], ((Long) object) * ((Long) data));
                    break;
                case $PUSH:
                    ((ArrayList) data).add(object);
                    break;
                case $PULL:
                    ((ArrayList) data).remove(object);
                    break;
                default:
                    break;
            }
        }

        if (updateDatabase)
            if (async)
                MongoAccessThread.submitQuery(new SingleUpdateQuery<>(DatabaseInstance.playerData, Filters.eq("info.uuid", uuid.toString()), new Document(EO.getUO(), new Document(variable, object)), doAfterOptional));
            else {
                UpdateResult result = DatabaseInstance.playerData.updateOne(Filters.eq("info.uuid", uuid.toString()), new Document(EO.getUO(), new Document(variable, object)), MongoAccessThread.uo);
                if (doAfterOptional != null)
                    doAfterOptional.accept(result);


                if (Bukkit.isPrimaryThread()) {
                    log.warning("[Database] Updating " + uuid.toString() + "'s player data on the main thread");

                }
            }
    }
    /**
     * Updates a players information in Mongo and returns the updated result.
     *
     * @param uuid
     * @param EO
     * @param variable
     * @param object
     * @param async
     * @param doAfterOptional an optional parameter allowing you to specify extra actions after the update query is
     *                        completed. doAfterOptional is executed async or sync based on the previous async parameter.
     * @since 1.0
     */
    public void update(UUID uuid, EnumOperators EO, EnumData variable, Object object, boolean async, boolean updateDatabase, Consumer<UpdateResult> doAfterOptional) {
        if (PLAYERS.containsKey(uuid)) { // update local data
            Document localDoc = PLAYERS.get(uuid);
            String[] key = variable.getKey().split("\\.");
            Document rootDoc = (Document) localDoc.get(key[0]);
            Object data = rootDoc.get(key[1]);
            switch (EO) {
                case $SET:
                    rootDoc.put(key[1], object);
                    break;
                case $INC:
                    if (data instanceof Integer)
                        rootDoc.put(key[1], ((Integer) object) + ((Integer) data));
                    else if (data instanceof Double)
                        rootDoc.put(key[1], ((Double) object) + ((Double) data));
                    else if (data instanceof Float)
                        rootDoc.put(key[1], ((Float) object) + ((Float) data));
                    else if (data instanceof Long)
                        rootDoc.put(key[1], ((Long) object) + ((Long) data));
                    break;
                case $MUL:
                    if (data instanceof Integer)
                        rootDoc.put(key[1], ((Integer) object) * ((Integer) data));
                    else if (data instanceof Double)
                        rootDoc.put(key[1], ((Double) object) * ((Double) data));
                    else if (data instanceof Float)
                        rootDoc.put(key[1], ((Float) object) * ((Float) data));
                    else if (data instanceof Long)
                        rootDoc.put(key[1], ((Long) object) * ((Long) data));
                    break;
                case $PUSH:
                    ((ArrayList) data).add(object);
                    break;
                case $PULL:
                    ((ArrayList) data).remove(object);
                    break;
                default:
                    break;
            }
        }

        if (updateDatabase)
            if (async)
                MongoAccessThread.submitQuery(new SingleUpdateQuery<>(DatabaseInstance.playerData, Filters.eq("info.uuid", uuid.toString()), new Document(EO.getUO(), new Document(variable.getKey(), object)), doAfterOptional));
            else {
                UpdateResult result = DatabaseInstance.playerData.updateOne(Filters.eq("info.uuid", uuid.toString()), new Document(EO.getUO(), new Document(variable.getKey(), object)), MongoAccessThread.uo);
                if (doAfterOptional != null)
                    doAfterOptional.accept(result);


                if (Bukkit.isPrimaryThread()) {
                    log.warning("[Database] Updating " + uuid.toString() + "'s player data on the main thread");

                }
            }
    }

    /**
     * {@link #update(UUID, EnumOperators, EnumData, Object, boolean, boolean, Consumer)}
     */
    public void update(UUID uuid, EnumOperators EO, EnumData variable, Object object, boolean async) {
        update(uuid, EO, variable, object, async, true, null);
    }

    /**
     * {@link #update(UUID, EnumOperators, EnumData, Object, boolean, boolean, Consumer)}
     */
    public void update(UUID uuid, EnumOperators EO, EnumData variable, Object object, boolean async, Consumer<UpdateResult> doAfterOptional) {
        update(uuid, EO, variable, object, async, true, doAfterOptional);
    }

    /**
     * {@link #update(UUID, EnumOperators, EnumData, Object, boolean, boolean, Consumer)}
     */
    public void update(UUID uuid, EnumOperators EO, EnumData variable, Object object, boolean async, boolean updateDatabase) {
        update(uuid, EO, variable, object, async, updateDatabase, null);
    }

    public Object getData(String data, UUID uuid) {
        Document doc;

        // GRABBED CACHED DOCUMENT
        if (PLAYERS.containsKey(uuid)) doc = PLAYERS.get(uuid);
        else {
            long currentTime = 0;
            // we should never be getting offline data sync.
            if (Bukkit.isPrimaryThread()) {
                log.info("[Database] Requested for " + uuid + "'s document from the database on the main thread.");
            }
            doc = DatabaseInstance.playerData.find(Filters.eq("info.uuid", uuid.toString())).first();
        }

        return getData(data, doc);
    }
    /**
     * Safely Returns the object that's requested
     * based on UUID.
     *
     * @param data Data type
     * @param uuid UUID
     * @return Requested data
     */
    public Object getData(EnumData data, UUID uuid) {
        Document doc;

        // GRABBED CACHED DOCUMENT
        if (PLAYERS.containsKey(uuid)) doc = PLAYERS.get(uuid);
        else {
            long currentTime = 0;
            // we should never be getting offline data sync.

            if (Bukkit.isPrimaryThread()) {
                log.info("[Database] Requested for " + uuid + "'s document from the database on the main thread.");
            }
            doc = DatabaseInstance.playerData.find(Filters.eq("info.uuid", uuid.toString())).first();
        }

        return getData(data, doc);
    }

    /**
     * Safely Returns the object that's requested
     * based on UUID.
     *
     * @param data     Data type
     * @param document User's document
     * @return Requested data
     */
    public Object getData(EnumData data, Document document) {
        if (document == null) return null;
        String[] key = data.getKey().split("\\.");
        Document rootDoc = (Document) document.get(key[0]);
        if (rootDoc == null) return null;

        Object dataObj = rootDoc.get(key[1]);

        if (dataObj == null) return null;
        Class<?> clazz = dataObj.getClass();

        return rootDoc.get(key[1], clazz);
    }

    public Object getData(String data, Document document) {
        if (document == null) return null;
        String[] key = data.split("\\.");
        Document rootDoc = (Document) document.get(key[0]);
        if (rootDoc == null) return null;
        Document dataObj = (Document) rootDoc.get(key[1]);
        if (dataObj == null) return null;
        Object obj = dataObj.get(key[2]);
        Class<?> clazz = obj.getClass();
        return dataObj.get(key[2], clazz);
    }

    /**
     * Retrieve's user's document asynchronously
     *
     * @param ipAddress User's IP address
     * @param doAfter   Executed after document is retrieved
     */
    public void retrieveDocumentFromAddress(String ipAddress, Consumer<Document> doAfter) {
        MongoAccessThread.submitQuery(new DocumentSearchQuery<>(DatabaseInstance.playerData, Filters.eq("info.ipAddress", ipAddress), doAfter));
    }

    /**
     * Retrieve's user's document asynchronously
     *
     * @param uuid    User's UUID
     * @param doAfter Executed after document is retrieved
     */
       public void retrieveDocumentFromUUID(UUID uuid, Consumer<Document> doAfter) {
           MongoAccessThread.submitQuery(new DocumentSearchQuery<>(DatabaseInstance.playerData, Filters.eq("info.uuid", uuid.toString()), doAfter));
       }

    /**
     * Retrieve's user's document asynchronously
     *
     * @param username User's username
     * @param doAfter  Executed after document is retrieved
     */
    public void retrieveDocumentFromUsername(String username, Consumer<Document> doAfter) {
        MongoAccessThread.submitQuery(new DocumentSearchQuery<>(DatabaseInstance.playerData, Filters.eq("info.username", username.toLowerCase()), doAfter));
    }


    /**
     * Is fired to grab a player from Mongo
     * if they don't exist. Fire addNewPlayer() creation.
     *
     * @param uuid
     * @since 1.0
     */
    public void requestPlayer(UUID uuid, boolean async) {
        requestPlayer(uuid, async, null);
    }

    /**
     * Is fired to grab a player from Mongo
     * if they don't exist. Fire addNewPlayer() creation.
     *
     * @param uuid
     * @since 1.0
     */
    public void requestPlayer(UUID uuid, boolean async, Runnable doAfterOptional) {
        if (async) {
            retrieveDocumentFromUUID(uuid, doc -> {
                if (doc == null) {
                    addNewPlayer(uuid, async);
                } else {
                    PLAYERS.put(uuid, doc);
                }
                if (doAfterOptional != null)
                    doAfterOptional.run();
            });
        } else {
            Document doc = DatabaseInstance.playerData.find(Filters.eq("info.uuid", uuid.toString())).first();
            if (doc == null) addNewPlayer(uuid, async);
            else PLAYERS.put(uuid, doc);


            if (Bukkit.isPrimaryThread()) {
                log.info("[Database] Requested for " + uuid + "'s document from the database on the main thread.");

            }

            if (doAfterOptional != null)
                doAfterOptional.run();
        }
    }

    public String getUUIDFromName(String playerName) {
        if (CACHED_UUIDS.containsKey(playerName)) return CACHED_UUIDS.get(playerName);


        if (Bukkit.isPrimaryThread()) {
            log.warning("[Database] Retrieving " + playerName + " 's UUID from name on the main thread.");


        }

        Document doc = DatabaseInstance.playerData.find(Filters.eq("info.username", playerName.toLowerCase())).first();
        if (doc == null) return "";
        String uuidString = ((Document) doc.get("info")).get("uuid", String.class);
        CACHED_UUIDS.put(playerName, uuidString);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                CACHED_UUIDS.remove(playerName);
            }
        }, 500);
        return uuidString;
    }


    public String getOfflineName(UUID uuid) {
        if (Bukkit.isPrimaryThread()) {
            log.info("[Database] Retrieving for " + uuid.toString() + "'s name on the main thread.");

        }

        Document doc = DatabaseInstance.playerData.find(Filters.eq("info.uuid", uuid.toString())).first();
        if (doc == null) return "";
        else {
            return ((Document) doc.get("info")).get("username", String.class);
        }
    }


    /**
     * Adds a new player to Mongo Creates Document here.
     *
     * @param uuid
     * @since 1.0
     */

    private void addNewPlayer(UUID uuid, boolean async) {
        Document newPlayerDocument = new Document("info", new Document("uuid", uuid.toString())
                .append("username", "")
                .append("usernameHistory", Arrays.asList(""))
                .append("ipAddress", "")
                .append("firstLogin", System.currentTimeMillis() / 1000L)
                .append("lastLogin", 0L)
                .append("lastLogout", 0L)
                .append("rank", "default")
                .append("isNew", true)
                .append("currentCharacter", 0)
                .append("ipAddressHistory", Arrays.asList(""))
                .append("isPlaying", true));
        System.out.println(newPlayerDocument.toJson());
        DatabaseInstance.playerData.insertOne(newPlayerDocument);
        requestPlayer(uuid, async);
        log.info("[Database] Requesting new data for : " + uuid);
    }

    public void stopInvocation() {
        SERVER_EXECUTOR_SERVICE.shutdown();
    }
}
