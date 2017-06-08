package com.riddlesvillage.core.database.query;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import org.bson.conversions.Bson;

public abstract class Query<T> {

    private final Bson searchQuery;

    private final MongoCollection collection;

    private final SingleResultCallback<T> doAfter;

    /**
     * @param collection  Database collection
     * @param searchQuery Search query
     * @param doAfter     Consumer task to do after query is complete.
     */
    public Query(MongoCollection collection, Bson searchQuery, SingleResultCallback<T> doAfter) {
        this.collection = collection;
        this.searchQuery = searchQuery;
        this.doAfter = doAfter;
    }

    public Bson getSearchQuery() {
        return searchQuery;
    }

    public MongoCollection getCollection() {
        return collection;
    }

    public SingleResultCallback<T> getDoAfter() {
        return doAfter;
    }
}
