package com.riddlesvillage.core.database.query;

import com.mongodb.client.MongoCollection;
import com.riddlesvillage.core.database.data.Query;
import org.bson.conversions.Bson;

import java.util.function.Consumer;

public class DocumentSearchQuery<Document> extends Query<Document> {

    /**
     * @param collection  Database collection
     * @param searchQuery Search query
     * @param doAfter     Consumer task to do after query is complete.
     */
    public DocumentSearchQuery(MongoCollection collection, Bson searchQuery, Consumer<Document> doAfter) {
        super(collection, searchQuery, doAfter);
    }

}
