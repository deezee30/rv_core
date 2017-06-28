package com.riddlesvillage.core.backend.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.riddlesvillage.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew E on 6/11/2017.
 */
public class BungeeServerListener implements PluginMessageListener {
    private Map<String, Integer> playerCountMap;
    private Map<String, Integer> maxPlayerCountMap;
    private List<String> serverList;
    private String server;

    private static BungeeServerListener instance;

    public static BungeeServerListener getInstance() {
        if (instance == null) {
            instance = new BungeeServerListener();
        }
        return instance;
    }


    public BungeeServerListener() {
        instance = this;
        this.playerCountMap = new HashMap<>();
        this.maxPlayerCountMap = new HashMap<>();
        this.serverList = new ArrayList<>();
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Core.get(), "BungeeCord");
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(Core.get(), "BungeeCord", this);
        Core.debug("&7[&aBungeeListener&7] &7Now listening on BungeeCord messages.");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.get(), () -> {
            long startTime = System.currentTimeMillis();
            Core.debug("&7[&aBungeeListener&7] &7Updating cache...");

        }, 60L, 60L);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equals("GetServers")) {
            long startTime = System.currentTimeMillis();
            String[] servers = in.readUTF().split(", ");
            for (String server : servers) {
                if (!serverList.contains(server)) {
                    serverList.add(server);
                    long time = (System.currentTimeMillis() - startTime);
                    Core.debug("&7[&aBungeeListener&7] &7Caching server &a{0} &7in &e{1}ms", server, time + "");
                }
            }
        }
        if (subChannel.equals("GetServer")) {
            long startTime = System.currentTimeMillis();
            this.server = in.readUTF();
            Core.debug("&7[&aBungeeListener&7] &7Cached server name &7in &e{0}ms", (System.currentTimeMillis() - startTime) + "");
        }
        if (subChannel.equals("PlayerCount")) {
            long startTime = System.currentTimeMillis();
            String server = in.readUTF(); // Name of server, as given in the arguments
            int playerCount = in.readInt();
            if (this.playerCountMap.containsKey(server)) {
                this.playerCountMap.remove(server);
            }
            this.playerCountMap.put(server, playerCount);
            long time = (System.currentTimeMillis() - startTime);
            Core.debug("&7[&aBungeeListener&7] &7Cached player count for &a{0} &7in &e{1}ms", server, time + "");
        }
    }
}
