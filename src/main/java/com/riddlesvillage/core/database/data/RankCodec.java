/*
 * rv_core
 * 
 * Created on 08 June 2017 at 11:30 PM.
 */

package com.riddlesvillage.core.database.data;

import com.riddlesvillage.core.player.Rank;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public final class RankCodec implements Codec<Rank> {

    @Override
    public Rank decode(final BsonReader reader,
                       final DecoderContext decoderContext) {
        return Rank.byName(reader.readString());
    }

    @Override
    public void encode(final BsonWriter writer,
                       final Rank value,
                       final EncoderContext encoderContext) {
        writer.writeString(value.getName());
    }

    @Override
    public Class<Rank> getEncoderClass() {
        return Rank.class;
    }
}