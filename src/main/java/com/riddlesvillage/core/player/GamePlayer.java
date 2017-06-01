package com.riddlesvillage.core.player;

import com.riddlesvillage.core.RiddlesVillageCore;
import com.riddlesvillage.core.api.CoreProfile;
import com.riddlesvillage.core.api.file.yaml.YamlFile;
import com.riddlesvillage.core.api.file.yaml.YamlFileImpl;
import com.riddlesvillage.core.api.file.yaml.YamlLoadException;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.database.data.EnumData;
import com.riddlesvillage.core.database.data.EnumOperators;
import com.riddlesvillage.core.player.rank.Rank;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;


public final class GamePlayer implements CoreProfile, Serializable {

    private Player player;
    private String server;
    private long firstJoin;
    private long lastLogin;
    private boolean online;
    private long lastLogout;
    private long playTime;
    private String ipAddress;
    private List<String> ipAddressHistory;
    private List<String> usernameHistory;
    private Rank rank;
    private boolean isNew;
    private boolean playing;

    GamePlayer(Player player) {
        this.player = player;
        UUID id = player.getUniqueId();
        String name = player.getName();

        playing = (Boolean) DatabaseAPI.getInstance().getData(EnumData.IS_PLAYING, id);
        usernameHistory = (List<String>) DatabaseAPI.getInstance().getData(EnumData.USERNAME_HISTORY, id);
        if (!usernameHistory.contains(name)) {
            usernameHistory.add(name);
        }
        isNew = false;//(boolean) api.getData(EnumData.IS_NEW, uuid);
        ipAddress = (String) DatabaseAPI.getInstance().getData(EnumData.IP_ADDRESS, id);
        if (!player.getAddress().getHostName().replace("/", "").equals(ipAddress)) {
            ipAddress = player.getAddress().getHostName().replace("/", "");
        }
        ipAddressHistory = (List<String>) DatabaseAPI.getInstance().getData(EnumData.IP_ADDRESS_HISTORY, id);
        if (!ipAddressHistory.contains(player.getAddress().getHostName().replace("/", ""))) {
            ipAddressHistory.add(player.getAddress().getHostName().replace("/", ""));
        }
        rank = Rank.valueOf(((String) DatabaseAPI.getInstance().getData(EnumData.RANK, id)).toUpperCase());
        // Bukkit.getScheduler().scheduleAsyncRepeatingTask(MysticalWars.getInstance(), this::task, 1000L, 1000L);
    }

    private void task() {
        //save();
    }

    public void save() {
        UUID id = getUuid();
        DatabaseAPI.getInstance().update(id, EnumOperators.$SET, EnumData.IP_ADDRESS_HISTORY, ipAddressHistory, true);
        DatabaseAPI.getInstance().update(id, EnumOperators.$SET, EnumData.USERNAME_HISTORY, usernameHistory, true);
        DatabaseAPI.getInstance().update(id, EnumOperators.$SET, EnumData.RANK, rank.toString().toLowerCase(), true);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public String getDisplayName() {
        return player.getDisplayName();
    }

    public UUID getUuid() {
        return player.getUniqueId();
    }

    @Override
    public boolean hasPlayed() {
        return !isNew;
    }

    public String getIpAddress() {
        return ipAddress ;
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

    public boolean isAdmin() {
        return rank.getId() >= Rank.LEAD_DEVELOPER.getId();
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
            String message = ChatColor.translateAlternateColorCodes('&', fileConfig.get().getString(key));
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
