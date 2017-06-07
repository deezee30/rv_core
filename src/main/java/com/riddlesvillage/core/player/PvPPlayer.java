package com.riddlesvillage.core.player;

import com.mongodb.async.client.MongoCollection;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import org.bson.Document;

/**
 * Created by Matthew E on 6/7/2017.
 */
public class PvPPlayer extends AbstractCoreProfile {
    private final CorePlayer player;
    private int deaths;
    private int assists;
    private int kills;

    PvPPlayer(CorePlayer player) {
        super(player.getUuid(), player.getName());
        this.player = player;

        // ... load the player instance as you do ...
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return Database.getMainCollection();
    }

    @Override
    public void loadStats(Document stats) {
        // update cached statistics as soon as they are downloaded async
       this.kills = stats.getInteger("kills");
        this.deaths = stats.getInteger("deaths");
        this.assists = stats.getInteger("assets");
    }

    @Override
    public CorePlayer toCorePlayer() {
        return player;
    }


}