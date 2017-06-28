/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player;

import com.google.common.base.Strings;
import com.mongodb.async.client.MongoCollection;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class OfflineCorePlayer extends AbstractCoreProfile {

    /*
     * Cached profiles may include names/uuids of players that may not exist.
     * To check if they are a real profile simply call #hasPlayed().
     */
    static final EnhancedList<OfflineCorePlayer> CACHED_PROFILES = new EnhancedList<>();

    static {
        // Refresh cached users every 30 seconds.
        new BukkitRunnable() {

            @Override
            public void run() {
                CACHED_PROFILES.forEach(profile -> profile.refreshStats());
            }
        }.runTaskTimerAsynchronously(Core.get(), 600L, 600L);
    }

    private Optional<Consumer<OfflineCorePlayer>>
            doAfter = Optional.empty();
    private transient Rank
            rank    = Rank.DEFAULT;
    private transient boolean
            premium = false;
    private transient int
            coins   = 0,
            tokens  = 0;

    private OfflineCorePlayer(Optional<UUID> uuid,
                              Optional<String> name,
                              Optional<Consumer<OfflineCorePlayer>> doAfter) {
        super(uuid, name);
        this.doAfter = doAfter;
    }

    OfflineCorePlayer(CorePlayer player) {
        super(Optional.of(player.getUuid()), Optional.of(player.getName()));
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GRAY + getName();
    }

    @Override
    public String getIp() {
        return null;
    }

    @Override
    public List<String> getIpHistory() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getNameHistory() {
        return Collections.emptyList();
    }

    @Override
    public Optional<MongoCollection<Document>> getCollection() {
        return Optional.of(Database.getMainCollection());
    }

    @Override
    public void onLoad(Optional<Document> doc) {
        if (doc.isPresent()) {
            Document stats = doc.get();

            rank = Rank.byName(stats.getString("rank"));
            premium = stats.getBoolean("premium");
            coins = stats.getInteger("coins");
            tokens = stats.getInteger("tokens");

            CACHED_PROFILES.addIf(!CACHED_PROFILES.contains(this), this);
        } else {
            Messaging.debug("Failed to obtain stats for '%s' ('%s')", getName(), getUuid());
        }

        // run the runnable once
        if (doAfter.isPresent()) {
            doAfter.get().accept(this);
            doAfter = Optional.empty();
        }
    }

    public static OfflineCorePlayer fromName(String name) {
        return get(get(name), Optional.empty(), Optional.of(name));
    }

    public static OfflineCorePlayer fromUuid(UUID uuid) {
        return get(get(uuid), Optional.of(uuid), Optional.empty());
    }

    public static void removeFromCache(UUID uuid) {
        Optional<OfflineCorePlayer> profile = get(uuid);
        if (profile.isPresent()) CACHED_PROFILES.remove(profile.get());
    }

    private static Optional<OfflineCorePlayer> get(String name) {
        for (OfflineCorePlayer profile : CACHED_PROFILES) {
            if (!Strings.isNullOrEmpty(profile.getName())
                    && profile.getName().equalsIgnoreCase(name)) {
                return Optional.of(profile);
            }
        }

        return Optional.empty();
    }

    private static Optional<OfflineCorePlayer> get(UUID id) {
        for (OfflineCorePlayer profile : CACHED_PROFILES) {
            if (profile.getUuid() != null
                    && profile.getUuid().equals(id)) {
                return Optional.of(profile);
            }
        }

        return Optional.empty();
    }

    private static OfflineCorePlayer get(Optional<OfflineCorePlayer> cache,
                                         Optional<UUID> uuid,
                                         Optional<String> name) {
        OfflineCorePlayer profile;

        if (cache.isPresent()) {
            profile = cache.get();
        } else {
            // wait for async download to finish, then resume operation
            CompletableFuture<OfflineCorePlayer> futureProfile = new CompletableFuture<>();
            profile = new OfflineCorePlayer(uuid, name, Optional.of(futureProfile::complete));

            // allow maximum 2 seconds of processing
            try {
                profile = futureProfile.get(2, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }

        return profile;
    }

    @Override
    public void _setCoins(int coins) {
        this.coins = coins;
    }

    @Override
    public void _setPremium(boolean premium) {
        this.premium = premium;
    }

    @Override
    public void _setRank(Rank rank) {
        this.rank = rank;
    }

    @Override
    public void _setTokens(int tokens) {
        this.tokens = tokens;
    }

    @Override
    public Rank getRank() {
        return rank;
    }

    @Override
    public boolean isPremium() {
        return premium;
    }

    @Override
    public int getCoins() {
        return coins;
    }

    @Override
    public int getTokens() {
        return tokens;
    }
}