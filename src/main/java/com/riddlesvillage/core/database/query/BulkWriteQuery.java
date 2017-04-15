package com.riddlesvillage.core.database.query;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import com.riddlesvillage.core.database.data.Query;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.function.Consumer;

public class BulkWriteQuery<BulkWriteResult> extends Query<BulkWriteResult> {

    private final List<UpdateOneModel<Document>> models;

    /**
     * @param collection  Database collection
     * @param doAfterOptional Consumer task to do after query is complete.
     * @param models          Write models
     */
    public BulkWriteQuery(MongoCollection collection, List<UpdateOneModel<Document>> models, Consumer<BulkWriteResult> doAfterOptional) {
        super(collection, null, doAfterOptional);
        this.models = models;
    }

    public BulkWriteQuery(MongoCollection collection, Bson searchQuery, Consumer<BulkWriteResult> doAfter, List<UpdateOneModel<Document>> models) {
        super(collection, searchQuery, doAfter);
        this.models = models;
    }

    public List<UpdateOneModel<Document>> getModels() {
        return models;
    }
}
