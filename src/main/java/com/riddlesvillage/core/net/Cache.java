package com.riddlesvillage.core.net;

import com.riddlesvillage.core.RiddlesCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cache {

	private static final Messenger MESSENGER = RiddlesCore.getMessenger();

	public HashMap<String, Integer> online = new HashMap<>();
	public HashMap<String, String> server = new HashMap<>();
	public HashMap<String, List<String>> playersOnlineServer = new HashMap<>();
	public List<String> allPlayersOnline = new ArrayList<>();
	public List<String> allServers = new ArrayList<>();
	public short playersOnline = 0;

	public Cache() {}

	public Cache(Boolean autoCache, Integer autoCacheHeartBeat, Plugin p) {
		if (autoCache) {
			Bukkit.getScheduler().runTaskTimerAsynchronously(p, () -> {
				for (String s : allServers) {
					MESSENGER.getAllServers();
					MESSENGER.getAllPlayersOnServer(s);
					MESSENGER.getServerCount(s);
				}
				MESSENGER.getAllPlayers();
				MESSENGER.getServerCount("ALL");
			}, autoCacheHeartBeat, autoCacheHeartBeat);
		}
	}
}
