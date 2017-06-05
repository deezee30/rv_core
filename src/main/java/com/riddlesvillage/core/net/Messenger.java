package com.riddlesvillage.core.net;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class Messenger implements PluginMessageListener {

	public Plugin plugin;
	public ByteArrayDataInput bytein;
	public Cache cache;

	public Messenger(Plugin j) {
		j.getServer().getMessenger().registerOutgoingPluginChannel(j, "BungeeCord");
		j.getServer().getMessenger().registerIncomingPluginChannel(j, "BungeeCord", this);
		plugin = j;
		cache = new Cache();
	}

	public Messenger(Plugin j, Boolean autoCache, Integer autoCacheHeartBeat) {
		j.getServer().getMessenger().registerOutgoingPluginChannel(j, "BungeeCord");
		j.getServer().getMessenger().registerIncomingPluginChannel(j, "BungeeCord", this);
		plugin = j;
		cache = new Cache(autoCache, autoCacheHeartBeat, j);
	}

	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		bytein = ByteStreams.newDataInput(message);
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		try {
			String subChannel = in.readUTF();
			if (subChannel.equals("KickPlayer")) {
				short size = in.readShort();
				byte[] bytes = new byte[size];
				in.readFully(bytes);
				DataInputStream cmdMsg = new DataInputStream(new ByteArrayInputStream(bytes));
				String reason = cmdMsg.readUTF();
				String executor = cmdMsg.readUTF();
				Player executingPlayer = Bukkit.getPlayer(executor);
				if (executingPlayer == null) {
					return;
				}
				executingPlayer.kickPlayer(colorString(reason));
			}
			if (subChannel.equals("AllServerMessage")) {
				short size = in.readShort();
				byte[] bytes = new byte[size];
				in.readFully(bytes);
				DataInputStream cmdMsg = new DataInputStream(new ByteArrayInputStream(bytes));
				String messageString = cmdMsg.readUTF();
				Bukkit.getServer().broadcastMessage(messageString);
			}
			if (subChannel.equals("Message")) {
				short size = in.readShort();
				byte[] bytes = new byte[size];
				in.readFully(bytes);
				DataInputStream cmdMsg = new DataInputStream(new ByteArrayInputStream(bytes));
				String msg2Send = cmdMsg.readUTF();
				String executor = cmdMsg.readUTF();
				Player executingPlayer = Bukkit.getPlayer(executor);
				if (executingPlayer == null) {
					return;
				}
				executingPlayer.sendMessage(colorString(msg2Send));
			}
			if (subChannel.equals("GetServers")) {
				String _temp = in.readUTF();
				_temp = _temp.replace(" and ", " ");
				_temp = _temp.replace(", ", " ");
				_temp = _temp.replace(" ", ", ");
				cache.allServers.clear();
				Collections.addAll(cache.allServers, _temp.split(", "));
			}
			if (subChannel.equals("GetServer")) {
				cache.server.remove(player.getName());
				cache.server.put(player.getName(), in.readUTF());

			}
			if (subChannel.equals("PlayerList")) {
				String plServer = in.readUTF();
				if (plServer.equalsIgnoreCase("ALL")) {
					String originalPlayerList = in.readUTF();
					cache.allPlayersOnline.clear();
					Collections.addAll(cache.allPlayersOnline, originalPlayerList.split(", "));
				} else {
					String originalPlayerList = in.readUTF();
					List<String> playersOnline = new ArrayList<>();
					Collections.addAll(playersOnline, originalPlayerList.split(", "));
					cache.playersOnlineServer.put(plServer, playersOnline);
				}
			}
			if (subChannel.equals("PlayerCount")) {
				String inUTF = in.readUTF();
				if (inUTF.equalsIgnoreCase("ALL")) {
					cache.playersOnline = in.readShort();
				} else {
					cache.online.put(inUTF, in.readInt());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public String colorString(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public Integer getServerCount(String server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerCount");
		out.writeUTF(server);
		try {
			sendAnonymous(out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cache.online.get(server);
	}

	public String getServer(String player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServer");
		try {
			sendAnonymous(out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cache.server.get(player);
	}

	public String getServer(Player player) {
		return getServer(player.getName());
	}

	public List<String> getAllPlayersOnServer(String server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerList");
		out.writeUTF(server);
		try {
			sendAnonymous(out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cache.playersOnlineServer.get(server);
	}

	public List<String> getAllPlayers() {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerList");
		out.writeUTF("ALL");
		try {
			sendAnonymous(out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cache.allPlayersOnline;
	}

	public List<String> getAllServers() {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServers");
		try {
			sendAnonymous(out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cache.allServers;
	}

	public void sendMsgToPlayer(String player, String message) {
		ByteArrayOutputStream msg = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(msg);
		try {
			out.writeUTF("Message");
			out.writeUTF(player);
			out.writeUTF(message);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendAnonymous(msg.toByteArray());
	}

	public void sendAllServerMessage(String message) {
		ByteArrayOutputStream msg = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(msg);
		try {
			out.writeUTF("AllServerMessage");
			out.writeUTF(message);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendAnonymous(msg.toByteArray());
	}

	public void kickPlayer(String player, String message) {
		ByteArrayOutputStream msg = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(msg);
		try {
			out.writeUTF("KickPlayerKickPlayerKickKickBecause");
			out.writeUTF(player);
			out.writeUTF(message);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendAnonymous(msg.toByteArray());
	}

	public List<Player> getOnlinePlayers() {
		List<Player> players = new ArrayList<>();
		for (World w : Bukkit.getWorlds()) {
			players.addAll(w.getPlayers().stream().collect(Collectors.toList()));
		}
		return players;
	}

	public void sendAnonymous(byte[] message) {
		if (getOnlinePlayers().size() < 1) {
			return;
		}
		getOnlinePlayers().get(0).sendPluginMessage(plugin, "BungeeCord", message);
	}

	public void sendTo(byte[] message, List<Player> players) {
		for (Player player : players) {
			player.sendPluginMessage(plugin, "BungeeCord", message);
		}
	}
}