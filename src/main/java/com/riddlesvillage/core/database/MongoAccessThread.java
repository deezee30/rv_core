package com.riddlesvillage.core.database;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.riddlesvillage.core.database.query.BulkWriteQuery;
import com.riddlesvillage.core.database.query.DocumentSearchQuery;
import com.riddlesvillage.core.database.query.Query;
import com.riddlesvillage.core.database.query.SingleUpdateQuery;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MongoAccessThread extends Thread {

    public static Queue<Query<?>> CONCURRENT_QUERIES = new ConcurrentLinkedQueue<>();

    public final static UpdateOptions UPDATE_OPTIONS = new UpdateOptions().upsert(true);

    private final ExecutorService CONSUMER_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    public static void submitQuery(Query<?> query) {
        CONCURRENT_QUERIES.add(query);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // ALLOW THREAD TO SLEEP FOR 15ms BEFORE CONTINUING QUEUE //
                Thread.sleep(15L);
            } catch (InterruptedException ignored) {}

            while (!CONCURRENT_QUERIES.isEmpty()) {
                Query<?> query = CONCURRENT_QUERIES.poll();
                if (query == null) continue;

                if (query instanceof SingleUpdateQuery) {
                    SingleUpdateQuery<UpdateResult> updateQuery = (SingleUpdateQuery<UpdateResult>) query;
                    updateQuery.getCollection().updateOne(
                            updateQuery.getSearchQuery(),
                            updateQuery.getNewDocument(),
                            UPDATE_OPTIONS,
                            new SingleResultCallback<UpdateResult>() {

                                @Override
                                public void onResult(UpdateResult updateResult, Throwable throwable) {
                                    if (updateQuery.getDoAfter() != null) {
                                        CONSUMER_EXECUTOR_SERVICE.submit(() -> updateQuery
                                                .getDoAfter().onResult(updateResult, throwable));
                                    }
                                }
                            });

                } else if (query instanceof DocumentSearchQuery) {
                    DocumentSearchQuery documentSearchQuery = (DocumentSearchQuery) query;
                    documentSearchQuery.getCollection()
                            .find(documentSearchQuery.getSearchQuery())
                            .first((result, throwable) -> CONSUMER_EXECUTOR_SERVICE.submit(
                                    new Runnable() {

                                        @Override
                                        public void run() {
                                            documentSearchQuery.getDoAfter().onResult(result, throwable);
                                        }
                                    }));

                } else if (query instanceof BulkWriteQuery) {
                    BulkWriteQuery<BulkWriteResult> bulkWriteQuery = (BulkWriteQuery<BulkWriteResult>) query;
                    bulkWriteQuery.getCollection().bulkWrite(bulkWriteQuery.getModels(), (bulkWriteResult, throwable) -> {
                        if (bulkWriteQuery.getDoAfter() != null) {
                            CONSUMER_EXECUTOR_SERVICE.submit(new Runnable() {

                                @Override
                                public void run() {
                                    bulkWriteQuery.getDoAfter().onResult((BulkWriteResult) bulkWriteResult, throwable);
                                }
                            });
                        }
                    });
                }
            }
        }

    }
}
