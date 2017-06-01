package com.riddlesvillage.core.player;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.riddlesvillage.core.api.mechanic.GameMechanic;
import com.riddlesvillage.core.api.scoreboard.PlayerScoreboard;
import com.riddlesvillage.core.api.scoreboard.ScoreBoardManager;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.database.data.EnumData;
import com.riddlesvillage.core.database.data.EnumOperators;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Created by matt1 on 3/22/2017.
 */
public final class PlayerHandler extends GameMechanic {

    private static final PlayerHandler HANDLER = new PlayerHandler();
    private static final GamePlayerManager MANAGER = GamePlayerManager.get();

    public static PlayerHandler getHandler() {
        return HANDLER;
    }

    // disable public instantiation
    private PlayerHandler() {}

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        DatabaseAPI api = DatabaseAPI.getInstance();
        api.requestPlayer(event.getUniqueId(), true, () -> {
            System.out.println("Player requested " + event.getUniqueId().toString());
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        handleLogin(event.getPlayer());
    }

    public void handleLogin(Player player) {
        MANAGER.add(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        GamePlayer gamePlayer = MANAGER.get(event);
        gamePlayer.setNew(false);
        handleLogout(event.getPlayer().getUniqueId(), true, bulkWriteResult -> {
            System.out.println("Player logged out " + event.getPlayer().getUniqueId().toString());
        });
    }

    public void handleLogout(UUID uuid, boolean async, Consumer<BulkWriteResult> doAfter) {
        Player player = Bukkit.getPlayer(uuid);
        try {
            DatabaseAPI.getInstance().update(uuid, EnumOperators.$SET, EnumData.IS_PLAYING, false, true, true);
        } catch (Exception e) {

        }
        savePlayerData(uuid, async, bulkWriteResult -> {
            List<UpdateOneModel<Document>> operations = new ArrayList<>();
            Bson searchQuery = Filters.eq("info.uuid", uuid.toString());
            if (!DatabaseAPI.getInstance().PLAYERS.containsKey(uuid)) {
                return;
            }
            operations.add(new UpdateOneModel<>(searchQuery, new Document(EnumOperators.$SET.getUO(), new Document(EnumData.LAST_LOGOUT.getKey(), System.currentTimeMillis()))));

            PlayerScoreboard scoreboard = ScoreBoardManager.getManager().getScoreboard(player);
            scoreboard.destroy();
            ScoreBoardManager.getManager().removeScoreboard(player);
            operations.add(new UpdateOneModel<>(searchQuery, new Document(EnumOperators.$SET.getUO(), new Document(EnumData.IS_PLAYING.getKey(), false))));

            DatabaseAPI.getInstance().bulkUpdate(operations, async, doAfterSave -> {
                DatabaseAPI.getInstance().PLAYERS.remove(player.getUniqueId());
                Logger.getLogger("PlayerHandler").info("Saved information for uuid: " + uuid.toString() + " on their logout.");

                if (doAfter != null)
                    doAfter.accept(doAfterSave);
            });
        });
    }

    public boolean savePlayerData(UUID uuid, boolean async, Consumer<BulkWriteResult> doAfter) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return false;
        }
        GamePlayer gamePlayer = MANAGER.get(uuid);
        gamePlayer.save();
        List<UpdateOneModel<Document>> operations = new ArrayList<>();
        Bson searchQuery = Filters.eq("info.uuid", uuid.toString());
        operations.add(new UpdateOneModel<>(searchQuery, new Document(EnumOperators.$SET.getUO(), new Document(EnumData.LAST_LOGOUT.getKey(), System.currentTimeMillis()))));
        operations.add(new UpdateOneModel<>(searchQuery, new Document(EnumOperators.$SET.getUO(), new Document(EnumData.IP_ADDRESS.getKey(), player.getAddress().getHostString().replace("/", "")))));
        operations.add(new UpdateOneModel<>(searchQuery, new Document(EnumOperators.$SET.getUO(), new Document(EnumData.USERNAME.getKey(), player.getName()))));
        operations.add(new UpdateOneModel<>(searchQuery, new Document(EnumOperators.$SET.getUO(), new Document(EnumData.RANK.getKey(), gamePlayer.getRank().toString().toLowerCase()))));
        MANAGER.remove(gamePlayer);
        DatabaseAPI.getInstance().bulkUpdate(operations, async, doAfter);
        return true;
    }

    public static GamePlayer get(String name) {
        return MANAGER.get(name);
    }

    public static GamePlayer get(UUID id) {
        return MANAGER.get(id);
    }
}
