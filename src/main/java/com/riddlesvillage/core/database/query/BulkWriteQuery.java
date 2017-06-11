package com.riddlesvillage.core.database.query;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public class BulkWriteQuery<BulkWriteResult> extends Query<BulkWriteResult> {

    private final List<UpdateOneModel<Document>> models;

    /**
     * @param collection  Database collection
     * @param doAfterOptional Consumer task to do after query is complete.
     * @param models          Write models
     */
    public BulkWriteQuery(MongoCollection<Document> collection,
                          List<UpdateOneModel<Document>> models,
                          SingleResultCallback<BulkWriteResult> doAfterOptional) {
        super(collection, null, doAfterOptional);
        this.models = models;
    }

    public BulkWriteQuery(MongoCollection<Document> collection,
                          Bson searchQuery,
                          SingleResultCallback<BulkWriteResult> doAfter,
                          List<UpdateOneModel<Document>> models) {
        super(collection, searchQuery, doAfter);
        this.models = models;
    }

    public List<UpdateOneModel<Document>> getModels() {
        return models;
    }
}
