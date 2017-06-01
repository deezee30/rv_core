package com.riddlesvillage.core.bungee;

import com.riddlesvillage.core.RiddlesVillageCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cache {

	public HashMap<String, Integer> online = new HashMap<String, Integer>();
	public HashMap<String, String> server = new HashMap<String, String>();
	public HashMap<String, List<String>> playersOnlineServer = new HashMap<String, List<String>>();
	public List<String> allPlayersOnline = new ArrayList<String>();
	public List<String> allServers = new ArrayList<String>();
	public short playersOnline = 0;

	public Cache() {
	}

	/**
	 *
	 * @param autoCache
	 * @param autoCacheHeartBeat
	 * @param p
	 */
	public Cache(Boolean autoCache, Integer autoCacheHeartBeat, Plugin p) {
		if (autoCache) {
			Bukkit.getScheduler().runTaskTimerAsynchronously(p, () -> {
				for (String s : allServers) {
					RiddlesVillageCore.getCore().getMessenger().getAllServers();
					RiddlesVillageCore.getCore().getMessenger().getAllPlayersOnServer(s);
					RiddlesVillageCore.getCore().getMessenger().getServerCount(s);
				}
				RiddlesVillageCore.getCore().getMessenger().getAllPlayers();
				RiddlesVillageCore.getCore().getMessenger().getServerCount("ALL");
			}, autoCacheHeartBeat, autoCacheHeartBeat);
		}
	}
}
