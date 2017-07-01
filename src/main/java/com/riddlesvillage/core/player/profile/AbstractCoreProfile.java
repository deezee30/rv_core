/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.profile;

import com.google.common.collect.ImmutableList;
import com.mongodb.async.client.MongoCollection;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.CoreException;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.OfflineCorePlayer;
import com.riddlesvillage.core.player.statistic.*;
import com.riddlesvillage.core.service.timer.Timer;
import com.riddlesvillage.core.util.UUIDUtil;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * An abstract version of expanded {@link CoreProfile}.
 *
 * <p>This class should be used for any scenario where a
 * virtual server user is involved who is expected to be
 * interacted with a Mongo database, regardless whether
 * he could be online or offile.  This profile is designed
 * to store the fundamental data for online and offline
 * players, who are eligible for interaction with the
 * database.</p>
 *
 * <p>Provided that the sub class holds this data for
 * an online player, this class is expected to be instantiated
 * only once, per online player, per server.  Otherwise,
 * there is no limit on how many profiles can be made.</p>
 *
 * <p>Upon instantiation, the essential data is requested
 * from the database.  If the user was not found in the
 * database (or the database connection is closed), either
 * the {@link UUID} or name of the user can end up being
 * {@code null} (but not both).  This still allows offline
 * profiles to store it in some sort of cache.  An example
 * of profile caching system can be found in the sub class
 * {@link OfflineCorePlayer}.
 *
 * <p>To check if the {@code UUID} and name of the player
 * are both real and thus making the user a valid user, simply
 * check with {@link #hasPlayed()}.</p>
 *
 * @see CoreProfile
 * @see CorePlayer
 * @see OfflineCorePlayer
 */
public abstract class AbstractCoreProfile implements StatisticHolder, PremiumHolder, CoinsHolder, TokensHolder, RankedPlayer {

    /*
     * In case the player is offline and the UUID or name has
     * not been found in the general database, either the UUID
     * or name can result to being null, as it can be used to
     * store for cache purposes.
     *
     * If both values are null, profile will not be generated.
     */
    private volatile UUID   uuid = null;
    private volatile String name = null;

    /**
     * Used for timing this class.  Can be accessed and interacted
     * with via sub classes.  It is recommented to stop the timer
     * after all loading is done via {@link Timer#forceStop()}.
     */
    protected volatile transient Timer
            timer           = new Timer();

    protected transient boolean
            loaded          = false,
            played          = false;
    private transient StatisticHolder
            statisticHolder = this;

    /**
     * @see #AbstractCoreProfile(Optional, Optional)
     */
    protected AbstractCoreProfile(final UUID uuid) {
        this(Optional.of(uuid), Optional.empty());
    }

    /**
     * @see #AbstractCoreProfile(Optional, Optional)
     */
    protected AbstractCoreProfile(final String name) {
        this(Optional.empty(), Optional.of(name));
    }

    /**
     * @see #AbstractCoreProfile(Optional, Optional)
     */
    protected AbstractCoreProfile(final UUID uuid,
                                  final String name) {
        this(Optional.of(uuid), Optional.of(name));
    }

    /**
     * Instantiates a user instance used to store essential data.
     *
     * <p>Either the {@param uuid} or {@param name} provided
     * can be {@link Optional} but not both.  This class attempts
     * to find the user in the pre-defined database collection.
     * If the database connection is not set up or the user
     * has not been found, the profile data will not be loaded.</p>
     *
     * <p>Class instatiation should be done so in {@link #onLoad(Optional)},
     * as described in {@link #onLoad(Optional)} JavaDocs.</p>
     *
     * @param   uuid
     *          A {@code UUID} that the user may have.
     * @param   name
     *          A name that the user may have.
     * @see     #onLoad(Optional)
     */
    protected AbstractCoreProfile(final Optional<UUID> uuid,
                                  final Optional<String> name) {
        if (!uuid.isPresent() && !name.isPresent()) {
            Core.log("Created a fake player but both ID and name are null");
            return;
        }

        this.uuid = uuid.isPresent() ? uuid.get() : null;
        this.name = name.isPresent() ? name.get() : null;

        // Record how long it takes to load the profile
        timer.start().onFinishExecute(() -> Core.debug(
                "Generated player profile (%s) '%s' with ID '%s' in %sms",
                AbstractCoreProfile.this.getClass().getSimpleName(),
                AbstractCoreProfile.this.name,
                AbstractCoreProfile.this.uuid,
                timer.getTime(TimeUnit.MILLISECONDS)
        ));

        Bukkit.getScheduler().runTaskAsynchronously(Core.get(), () -> {
            DataInfo data = DataInfo.UUID;
            Object value = this.uuid;

            // if uuid is null, use name instead
            if (!uuid.isPresent()) {
                data = DataInfo.NAME;
                value = this.name;
            }

            DatabaseAPI.retrieveDocument(
                    Database.getMainCollection(),
                    data, value, (document, throwable) -> {

                Core.logIf(
                        throwable != null,
                        "Error loading '%s' ('%s'):",
                        this.name,
                        this.uuid,
                        throwable
                );

                Optional<Document> downloadedDoc = Optional.ofNullable(document);
                if (downloadedDoc.isPresent()) {
                    played = true;

                    this.name = document.getString(DataInfo.NAME.getStat());
                    this.uuid = UUIDUtil.fromString(document.getString(DataInfo.UUID.getStat()));

                    Optional<MongoCollection<Document>> col = getCollection();
                    if (!col.isPresent() || col.get().equals(Database.getMainCollection())) {
                        // Apply already downloaded stats
                        finishLoading(downloadedDoc);
                    } else {
                        // Async download custom stats from database
                        refreshStats();
                    }
                } else {
                    // player never played before
                    finishLoading(Optional.<Document>empty());
                }
            });
        });
    }

    private void finishLoading(Optional<Document> document) {
        // finish loading on the main thread
        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.get(), () -> {
            loaded = true;
            if (isOnline()) played = true;
            onLoad(document);
            timer.forceStop();
        });
    }

    /**
     * Sets up and loads the player after the statistics have
     * been downloaded.
     *
     * <p>Because the Core relies on processing database tasks
     * asynchronously, this method is essential for such calls
     * and will get called as soon as the process is finished
     * and returned.</p>
     *
     * <p>Whenever a player instance is being set up and needs
     * to access certain statistics from {@link #getCollection()}.
     * the initialization of instance should be made in this
     * method.</p>
     *
     * <p>If the collection isn't provided by the sub class
     * <b>OR</b> the collection is the default collection
     * found at {@link Database#getMainCollection()}, then
     * the default collection statistics will be passed
     * in the arguments, in case the profile needs them to
     * finish loading, eg: {@link CorePlayer}.</p>
     *
     * <p>If the player is not found in the database, ie: the player hasn't
     * played the server before - then {@link Optional#empty()} is passed.</p>
     *
     * @param document the optional document that holds the statistics
     */
    protected abstract void onLoad(Optional<Document> document);

    /**
     * Redownload the {@link #getCollection()} again asynchronously
     * and call {@link #onLoad(Optional)} with the newly downloaded
     * stats.
     *
     * <p>Typically used for updating cached offline players</p>
     */
    protected void refreshStats() {
        // Async download custom stats from database
        DatabaseAPI.retrieveDocument(getCollection().get(), DataInfo.UUID, uuid, ((result, t) -> {
            Core.logIf(t != null, "Error loading '%s' ('%s'): %s", name, uuid, t);
            finishLoading(Optional.ofNullable(result));
        }));
    }

    @Override
    public String toString() {
        return name;
    }

    // == User Data ====================================================== //


    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public final boolean hasPlayed() {
        return played;
    }

    /**
     * @return  whether or not the instance has finished
     *          database lookup and is loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    // == Defaults ====================================================== //

    @Override
    public String getDisplayName() {
        CorePlayer player = toCorePlayer();
        if (player == null) {
            return (getUuid() == null ?
                    OfflineCorePlayer.fromName(getName()) :
                    OfflineCorePlayer.fromUuid(getUuid())
            ).getDisplayName();
        } else {
            return player.getDisplayName();
        }
    }

    @Override
    public String getIp() {
        CorePlayer player = toCorePlayer();
        if (player == null) {
            return (getUuid() == null ?
                    OfflineCorePlayer.fromName(getName()) :
                    OfflineCorePlayer.fromUuid(getUuid())
            ).getIp();
        } else {
            return player.getIp();
        }
    }

    @Override
    public List<String> getIpHistory() {
        CorePlayer player = toCorePlayer();
        if (player == null) {
            return (getUuid() == null ?
                    OfflineCorePlayer.fromName(getName()) :
                    OfflineCorePlayer.fromUuid(getUuid())
            ).getIpHistory();
        } else {
            return player.getIpHistory();
        }
    }

    @Override
    public List<String> getNameHistory() {
        CorePlayer player = toCorePlayer();
        if (player == null) {
            return (getUuid() == null ?
                    OfflineCorePlayer.fromName(getName()) :
                    OfflineCorePlayer.fromUuid(getUuid())
            ).getNameHistory();
        } else {
            return player.getNameHistory();
        }
    }

    // == Statistics ====================================================== //

    /**
     * Sets statistic holder.
     *
     * @param statisticHolder the statistic holder
     */
    public void setStatisticHolder(StatisticHolder statisticHolder) {
        this.statisticHolder = statisticHolder;
    }

    /**
     * Gets statistic holder.
     *
     * @return the statistic holder
     */
    public StatisticHolder getStatisticHolder() {
        return statisticHolder;
    }

    @Override
    public ImmutableList<String> getStatisticValues() {

        /*
         * If statHolder equals to the current instance (this
         * instance of CoreProfile), then return custom lines
         * for statistics.  Otherwise let statHolder decide
         * which statistics to return.
         */
        if (!equals(statisticHolder)) {
            return statisticHolder.getStatisticValues();
        }

        if (!played) {
            try {
                throw new CoreException("Player " + name + " never played before!");
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }

        return new ImmutableList.Builder<String>()
                .add("~")
                .add("~&e======= " + getDisplayName() + " &e=======")
                .add("~&eRank: " + getRank().getDisplayName())
                .add("~&eCoins: &3" + getCoins())
                .add("~&eTokens: &3" + getTokens())
                .add("~&ePremium: " + (isPremium() ? "&2True" : "&4False"))
                .add("~&eCurrently " + (isOnline() ? "&2Online" : "&4Offline"))
                .add("~")
                .build();
    }
}