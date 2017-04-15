package com.riddlesvillage.core.player;

import com.riddlesvillage.core.RiddlesVillageCore;
import com.riddlesvillage.core.api.file.yaml.YamlFile;
import com.riddlesvillage.core.api.file.yaml.YamlFileImpl;
import com.riddlesvillage.core.api.file.yaml.YamlLoadException;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.database.data.EnumData;
import com.riddlesvillage.core.database.data.EnumOperators;
import com.riddlesvillage.core.player.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


public class GamePlayer {

    private UUID uniqueId;
    private String server;
    private long firstJoin;
    private long lastLogin;
    private boolean online;
    private long lastLogout;
    private long playTime;
    private UUID uuid;
    private String username;
    private String ipAddress;
    private List<String> ipAddressHistory;
    private List<String> usernameHistory;
    private Rank rank;
    private boolean isNew;
    private boolean playing;

    public GamePlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
        this.playing = (Boolean) DatabaseAPI.getInstance().getData(EnumData.IS_PLAYING, this.uuid);
        this.usernameHistory = (List<String>) DatabaseAPI.getInstance().getData(EnumData.USERNAME_HISTORY, this.uuid);
        if (!usernameHistory.contains(username)) {
            usernameHistory.add(username);
        }
        this.isNew = false;//(boolean) api.getData(EnumData.IS_NEW, uuid);
        this.ipAddress = (String) DatabaseAPI.getInstance().getData(EnumData.IP_ADDRESS, uuid);
        if (!player.getAddress().getHostName().replace("/", "").equals(ipAddress)) {
            this.ipAddress =player.getAddress().getHostName().replace("/", "");
        }
        this.ipAddressHistory = (List<String>) DatabaseAPI.getInstance().getData(EnumData.IP_ADDRESS_HISTORY, this.uuid);
        if (!ipAddressHistory.contains(player.getAddress().getHostName().replace("/", ""))) {
            ipAddressHistory.add(player.getAddress().getHostName().replace("/", ""));
        }
        this.rank = Rank.valueOf(((String)DatabaseAPI.getInstance().getData(EnumData.RANK, uuid)).toUpperCase());
        PlayerHandler.getHandler().GAMEPLAYERS.put(username, this);
        // Bukkit.getScheduler().scheduleAsyncRepeatingTask(MysticalWars.getInstance(), this::task, 1000L, 1000L);
    }

    private void task() {
        //save();
    }

    public void save() {
        DatabaseAPI.getInstance().update(uuid, EnumOperators.$SET, EnumData.IP_ADDRESS_HISTORY, ipAddressHistory, true);
        DatabaseAPI.getInstance().update(uuid, EnumOperators.$SET, EnumData.USERNAME_HISTORY, usernameHistory, true);
        DatabaseAPI.getInstance().update(uuid, EnumOperators.$SET, EnumData.RANK, rank.toString().toLowerCase(), true);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public List<String> getIpAddressHistory() {
        return ipAddressHistory;
    }

    public List<String> getUsernameHistory() {
        return usernameHistory;
    }

    public Rank getRank() {
        return rank;
    }

    public static boolean isAdmin(Player player) {
        GamePlayer gamePlayer = PlayerHandler.getHandler().GAMEPLAYERS.get(player.getName());
        if (gamePlayer == null) {
            return false;
        }
        return (gamePlayer.getRank().getId() >= Rank.LEAD_DEVELOPER.getId());
    }

    public void lang(String key, String defaultLanguage, String... replace) {
        try {
            YamlFile fileConfig = new YamlFileImpl().load(new File(RiddlesVillageCore.getCore().getDataFolder() + "/language/", "messages.yml"));
            if(!fileConfig.isSet(key)) {
                fileConfig.set(key, defaultLanguage);
                try {
                    fileConfig.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String message = net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', fileConfig.get().getString(key));
            for (int i = 0; i < replace.length;i++) {
                message = message.replaceAll("\\{" + i + "}", replace[i]);
            }
            getPlayer().sendMessage(message);
        } catch (YamlLoadException e) {
            e.printStackTrace();
        }

    }

    public boolean isNew() {
        return isNew;
    }

    public GamePlayer setNew(boolean aNew) {
        isNew = aNew;
        return this;
    }

    public boolean isPlaying() {
        return playing;
    }
}
