package com.riddlesvillage.core.database.query;

import com.mongodb.async.client.MongoCollection;
import org.bson.conversions.Bson;

import java.util.function.Consumer;

public class SingleUpdateQuery<UpdateResult> extends Query<UpdateResult> {

    private final Bson newDocument;

    /**
     * @param collection  Database collection
     * @param searchQuery     Search query
     * @param newDocument     New Document to replace
     * @param doAfterOptional Consumer task to do after query is complete.
     */
    public SingleUpdateQuery(MongoCollection collection,
                             Bson searchQuery,
                             Bson newDocument,
                             Consumer<UpdateResult> doAfterOptional) {
        super(collection, searchQuery, doAfterOptional);
        this.newDocument = newDocument;
    }

    public Bson getNewDocument() {
        return newDocument;
    }
}
