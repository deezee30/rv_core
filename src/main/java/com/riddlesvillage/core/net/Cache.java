package com.riddlesvillage.core.net;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cache {

	/* TODO: Organise this class!!! >:( */

	private final Messenger messenger;
	private final Plugin plugin;
	private final int heartBeat;

	public HashMap<String, Integer> online = new HashMap<>();
	public HashMap<String, String> server = new HashMap<>();
	public HashMap<String, List<String>> playersOnlineServer = new HashMap<>();
	public List<String> allPlayersOnline = new ArrayList<>();
	public List<String> allServers = new ArrayList<>();
	public short playersOnline = 0;

	public Cache(Messenger messenger, Plugin plugin) {
		this(messenger, plugin, 60);
	}

	public Cache(Messenger messenger, Plugin plugin, int heartBeat) {
		this.messenger = messenger;
		this.plugin = plugin;
		this.heartBeat = heartBeat;
	}

	public void start() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			for (String s : allServers) {
				messenger.getAllServers();
				messenger.getAllPlayersOnServer(s);
				messenger.getServerCount(s);
			}
			messenger.getAllPlayers();
			messenger.getServerCount("ALL");
		}, heartBeat, heartBeat);
	}
}
