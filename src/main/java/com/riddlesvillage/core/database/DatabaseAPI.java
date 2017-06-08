package com.riddlesvillage.core.database;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.result.UpdateResult;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.data.DataOperator;
import com.riddlesvillage.core.database.query.BulkWriteQuery;
import com.riddlesvillage.core.database.query.DocumentSearchQuery;
import com.riddlesvillage.core.database.query.SingleUpdateQuery;
import org.apache.commons.lang.Validate;
import org.bson.Document;
import org.bson.codecs.Codec;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class DatabaseAPI {

    /**
     * @param operations      Bulk write operations
     * @param doAfterOptional an optional parameter allowing you to
     *                        specify extra actions after the update query is
     *                        completed. doAfterOptional is executed async.
     */
    public static void bulkUpdate(List<UpdateOneModel<Document>> operations,
                                  Consumer<BulkWriteResult> doAfterOptional) {
		Validate.notNull(operations);

        MongoAccessThread.submitQuery(new BulkWriteQuery<>(
                Database.getMainCollection(), operations, doAfterOptional));
    }

    public static void update(MongoCollection<Document> collection,
                       UUID uuid,
                       DataOperator operator,
                       StatType variable,
                       Object object,
                       Consumer<UpdateResult> doAfterOptional) {
		Validate.notNull(collection);
		Validate.notNull(operator);
		Validate.notNull(variable);

        MongoAccessThread.submitQuery(new SingleUpdateQuery<>(
                collection,
                Filters.eq("uuid", uuid.toString()),
                new Document(operator.getOperator(),
                        new Document(variable.getStat(), checkForCodec(object))),
                doAfterOptional
        ));
    }

    /**
     * Safely Returns the object that's requested
     * based on UUID.
     *
     * @param uuid UUID
     * @param data Data type
     * @return Requested data
     */
    public static Object getData(UUID uuid, StatType data) {
        AtomicReference<Document> doc = new AtomicReference<>(null);

        Database.getMainCollection()
                .find(Filters.eq("uuid", uuid.toString()))
                .first((result, t) -> doc.set(result));

        return getData(doc.get(), data);
    }

    /**
     * Safely Returns the object that's requested
     * based on UUID.
     *
     * @param document User's document
     * @param data     Data type
     * @return Requested data
     */
    public static Object getData(Document document, StatType data) {
        return document.get(data.getStat());
    }

    public static <Obj> Obj getData(Document document, StatType data, Class<Obj> clazz) {
        return clazz.cast(document.get(data.getStat()));
    }

    public static void retrieveCoreDataFromUuid(UUID uuid, Consumer<Document> doAfter) {
        retrieveDocument(Database.getMainCollection(), DataInfo.UUID, uuid, doAfter);
    }

    public static void retrieveCoreDataFromName(String username, Consumer<Document> doAfter) {
        retrieveDocument(Database.getMainCollection(), DataInfo.NAME, username, doAfter);
    }

    public static void retrieveDocument(MongoCollection<Document> collection,
                                 StatType stat,
                                 Object value,
                                 Consumer<Document> doAfter) {
        Validate.notNull(collection);
        Validate.notNull(stat);

        MongoAccessThread.submitQuery(new DocumentSearchQuery<>(collection,
				Filters.eq(stat.getStat(), checkForCodec(value)), doAfter));
    }

    public static void insertNew(MongoCollection collection,
                                 Map<String, Object> map,
                                 SingleResultCallback<Void> callback) {
        Document insert = new Document();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            insert.append(entry.getKey(), checkForCodec(entry.getValue()));
        }

		collection.insertOne(insert, callback);
    }

	private static Object checkForCodec(Object obj) {
		if (obj == null) return null;
		if (!(obj instanceof Codec<?>)) return String.valueOf(obj);
		return obj;
	}
}
