/*
 * rv_core
 * 
 * Created on 03 July 2017 at 11:34 PM.
 */

package com.riddlesvillage.core.database;

import com.google.common.collect.Lists;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.data.DataOperator;
import com.riddlesvillage.core.database.value.Value;
import com.riddlesvillage.core.database.value.ValueType;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public interface Identity {

    UUID getIdentifier();

    MongoCollection<Document> getCollection();

    default StatType getStatType() {
        return DataInfo.UUID;
    }

    default Bson getSearchQuery() {
        return Filters.eq(getStatType().getStat(), getIdentifier());
    }

    default void retrieveDocument(final SingleResultCallback<Document> doAfter) {
        DatabaseAPI.retrieveDocument(getCollection(), getStatType(), getIdentifier(), doAfter);
    }

    default void insertNew(final Map<String, Object> map) {
        insertNew(map, (result, t) -> {
            if (Core.logIf(t != null,
                    "Could not insert new `%s` data with entries `%s`:",
                    Identity.this, map))
                t.printStackTrace();
        });
    }

    default void insertNew(final Map<String, Object> map,
                           final SingleResultCallback<Void> doAfter) {
        DatabaseAPI.insertNew(getCollection(), map, doAfter);
    }

    default void update(final StatType stat,
                        final Value<Object> value) {
        update(stat, value, (result, t) -> {
            if (Core.logIf(t != null,
                    "Could not update `%s` data with value `%s:%s`:",
                    Identity.this, value, value.getType()))
                t.printStackTrace();
        });
    }

    default void update(final StatType stat,
                        final Value<Object> value,
                        final SingleResultCallback<UpdateResult> doAfter) {
        Validate.notNull(stat);
        Validate.notNull(value);

        Object obj = value.getValue();
        DataOperator operator = DataOperator.$SET;
        ValueType type = value.getType();
        if (value.isInteger() && type != ValueType.SET) {
            int i = (Integer) obj;
            if (type.equals(ValueType.TAKE)) i = -i;
            obj = i;
        }

        DatabaseAPI.update(getCollection(), getIdentifier(), stat, obj, operator, doAfter);
    }

    default void update(final StatType stat,
                        final Object obj,
                        final DataOperator operator) {
        update(stat, obj, operator, (result, t) -> {
            if (Core.logIf(t != null,
                    "Could not update `%s` data with value `%s:%s`:",
                    Identity.this, obj, operator))
                t.printStackTrace();
        });
    }

    default void update(final StatType stat,
                        final Object obj,
                        final DataOperator operator,
                        final SingleResultCallback<UpdateResult> doAfter) {
        DatabaseAPI.update(getCollection(), getIdentifier(), stat, obj, operator, doAfter);
    }

    default void update(final Map<StatType, Value> operations) {
        update(operations, (result, t) -> {
            if (Core.logIf(t != null,
                    "Could not bulk update `%s` data with operations `%s`:",
                    Identity.this, operations))
                t.printStackTrace();
        });
    }

    default void update(final Map<StatType, Value> operations,
                        final SingleResultCallback<BulkWriteResult> doAfter) {
        List<WriteModel<Document>> writeModels = Lists.newArrayListWithCapacity(operations.size());

        Bson searchQuery = getSearchQuery();
        writeModels.addAll(operations
                .entrySet()
                .stream()
                .map(entry -> new UpdateOneModel<Document>(
                        searchQuery,
                        new Document(
                                DataOperator.$SET.getOperator(),
                                new Document(
                                        entry.getKey().getStat(),
                                        entry.getValue().getValue()
                                )
                        )
                )).collect(Collectors.toList())
        );

        update(writeModels, doAfter);
    }

    default void update(final List<WriteModel<Document>> operations) {
        update(operations, (result, t) -> {
            if (Core.logIf(t != null,
                    "Could not bulk update `%s` data with operations `%s`:",
                    Identity.this, operations))
                t.printStackTrace();
        });
    }

    default void update(final List<WriteModel<Document>> operations,
                        final SingleResultCallback<BulkWriteResult> doAfter) {
        DatabaseAPI.bulkWrite(getCollection(), operations, doAfter);
    }
}