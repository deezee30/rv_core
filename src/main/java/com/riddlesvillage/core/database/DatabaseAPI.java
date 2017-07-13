package com.riddlesvillage.core.database;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.data.DataOperator;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.codecs.configuration.CodecConfigurationException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DatabaseAPI {

    /**
     * @param operations      Bulk write operations
     * @param doAfterOptional an optional parameter allowing you to
     *                        specify extra actions after the update query is
     *                        completed. doAfterOptional is executed async.
     */
    public static void bulkWrite(final MongoCollection<Document> collection,
                                 final List<WriteModel<Document>> operations,
                                 final SingleResultCallback<BulkWriteResult> doAfterOptional) {
        Validate.notNull(collection);
        Validate.notNull(operations);

        collection.bulkWrite(operations, doAfterOptional);
    }

    public static void update(final MongoCollection<Document> collection,
                              final UUID uuid,
                              final StatType variable,
                              final Object object,
                              final DataOperator operator,
                              final SingleResultCallback<UpdateResult> doAfterOptional) {
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

    public static void retrieve(final MongoCollection<Document> collection,
                                final SingleResultCallback<List<Document>> callback) {
        Validate.notNull(collection);
        Validate.notNull(callback);

        collection.find().into(new LinkedList<>(), callback);
    }

    public static void retrieveDocument(final MongoCollection<Document> collection,
                                        final StatType stat,
                                        final Object value,
                                        final SingleResultCallback<Document> doAfter) {
        Validate.notNull(collection);
        Validate.notNull(stat);

        collection.find(Filters.eq(stat.getStat(), checkForCodec(value))).first(doAfter);
    }

    public static void insertNew(final MongoCollection<Document> collection,
                                 final Map<StatType, Object> map,
                                 final SingleResultCallback<Void> callback) {
        Validate.notNull(collection);
        Validate.notNull(map);

        Document insert = new Document();
        for (Map.Entry<StatType, Object> entry : map.entrySet()) {
            insert.append(entry.getKey().getStat(), checkForCodec(entry.getValue()));
        }

        collection.insertOne(insert, callback);
    }

    public static Object checkForCodec(final Object obj) {
        if (obj == null) return null;

        // we don't want mongodb driver to convert uuid to binary 0x03
        if (obj instanceof UUID) return obj.toString();

        try {
            // check if there is a codec for object
            Database.client.getSettings().getCodecRegistry().get(obj.getClass());
        } catch (CodecConfigurationException ignored) {
            // if not, simply return as String
            return String.valueOf(obj);
        }

        return obj;
    }
}
