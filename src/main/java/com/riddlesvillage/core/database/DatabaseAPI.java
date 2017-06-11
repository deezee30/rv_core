package com.riddlesvillage.core.database;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.data.DataOperator;
import org.apache.commons.lang.Validate;
import org.bson.Document;
import org.bson.codecs.configuration.CodecConfigurationException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class DatabaseAPI {

    /**
     * @param operations      Bulk write operations
     * @param doAfterOptional an optional parameter allowing you to
     *                        specify extra actions after the update query is
     *                        completed. doAfterOptional is executed async.
     */
    public static void bulkWrite(MongoCollection<Document> collection,
								 List<WriteModel<Document>> operations,
								 SingleResultCallback<BulkWriteResult> doAfterOptional) {
		Validate.notNull(collection);
		Validate.notNull(operations);

		collection.bulkWrite(operations, doAfterOptional);
    }

    public static void update(MongoCollection<Document> collection,
							  UUID uuid,
							  DataOperator operator,
							  StatType variable,
							  Object object,
							  SingleResultCallback<UpdateResult> doAfterOptional) {
		Validate.notNull(collection);
		Validate.notNull(uuid);
		Validate.notNull(operator);
		Validate.notNull(variable);

		collection.updateOne(
				Filters.eq(DataInfo.UUID.getStat(), uuid.toString()),
				new Document(operator.getOperator(), new Document(
						variable.getStat(),
						checkForCodec(object)
				)), doAfterOptional
		);
    }

    public static <Obj> Obj getData(Document document, StatType data, Class<Obj> clazz) {
        return clazz.cast(document.get(data.getStat()));
    }

    public static void retrieveCoreDataFromUuid(UUID uuid, SingleResultCallback<Document> doAfter) {
        retrieveDocument(Database.getMainCollection(), DataInfo.UUID, uuid, doAfter);
    }

    public static void retrieveCoreDataFromName(String username, SingleResultCallback<Document> doAfter) {
        retrieveDocument(Database.getMainCollection(), DataInfo.NAME, username, doAfter);
    }

    public static void retrieveDocument(MongoCollection<Document> collection,
										StatType stat,
										Object value,
										SingleResultCallback<Document> doAfter) {
        Validate.notNull(collection);
        Validate.notNull(stat);

		collection.find(Filters.eq(stat.getStat(), checkForCodec(value))).first(doAfter);
	}

    public static void insertNew(MongoCollection<Document> collection,
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
		// we don't want mongodb driver to convert uuid to binary 0x03
		if (obj instanceof UUID) return obj.toString();
		try {
			// check if there is a codec for object
			MongoClients.getDefaultCodecRegistry().get(obj.getClass());
		} catch (CodecConfigurationException ignored) {
			// if not, simply return as String
			return String.valueOf(obj);
		}

		return obj;
	}
}
