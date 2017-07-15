/*
 * rv_core
 * 
 * Created on 08 June 2017 at 3:29 AM.
 */

package example;

import com.mongodb.async.client.MongoCollection;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.database.StatType;
import com.riddlesvillage.core.database.value.Value;
import com.riddlesvillage.core.database.value.ValueType;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import com.riddlesvillage.core.util.MathUtil;
import org.bson.Document;

import java.util.Optional;

final class PvPPlayer extends AbstractCoreProfile {

    private static final MongoCollection<Document> COLLECTION = Database.getCollection("pvp");

    // make field names in database with default values 0
    private static final StatType KILL_STAT     = StatType.create("kills", 0);
    private static final StatType DEATH_STAT    = StatType.create("death", 0);

    // reference to core player is handy
    private final CorePlayer player;

    // statistics from database
    private int kills = 0, deaths = 0;

    PvPPlayer(CorePlayer player) {
        super(player.getUuid(), player.getName());
        this.player = player;

        // ... load the player instance as you do ...
    }

    @Override
    public void onLoad(Optional<Document> doc) {
        if (doc.isPresent()) {
            // update cached statistics as soon as they are downloaded async
            Document stats = doc.get();
            kills = stats.getInteger("kills");
            deaths = stats.getInteger("deaths");

            // ... load the rest of the player based on given data ...

            Core.log("Fully loaded PvPPlayer %s!", getName());
        } else {
            // handle error
            Core.log("Failed player lookup!");
        }
    }

    @Override
    public MongoCollection<Document> getCollection() {
        // return a custom collection or null if none
        return COLLECTION;
    }

    @Override
    public CorePlayer toCorePlayer() {
        return player;
    }

    public void addKill() {
        kills++;

        update(KILL_STAT,  new Value<>(1, ValueType.GIVE));
    }

    public int getKills() {
        return kills;
    }

    public void addDeath() {
        deaths++;

        update(DEATH_STAT, new Value<>(1, ValueType.GIVE));
    }

    public int getDeaths() {
        return deaths;
    }

    public double getKdr() {
        return MathUtil.getRatio(kills, deaths);
    }

    @Override
    public String toString() {
        return "PvPPlayer{" +
                "player=" + player +
                ", kills=" + kills +
                ", deaths=" + deaths +
                '}';
    }
}