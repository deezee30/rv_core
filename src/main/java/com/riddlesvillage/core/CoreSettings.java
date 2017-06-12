/*
 * RiddlesCore
 */

package com.riddlesvillage.core;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.riddlesvillage.core.chat.ChatBlockFilter;
import com.riddlesvillage.core.chat.ChatFilters;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.net.paster.PasteException;
import com.riddlesvillage.core.net.paster.Paster;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.util.StringUtil;
import com.riddlesvillage.core.util.inventory.item.IndexedItem;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public final class CoreSettings {

	private String defaultLocale = "english";

	private final EnhancedMap<String, String>
			messages			= new EnhancedMap<>();
	private final EnhancedMap<IndexedItem, Predicate<CorePlayer>>
			loginItems			= new EnhancedMap<>();
	private final EnhancedList<String>
			allowedCommands		= new EnhancedList<>(),
			locales				= new EnhancedList<>();
	private final EnhancedMap<String, CoreInventoryClickEvent>
			inventories			= new EnhancedMap<>();
	private final AtomicBoolean
			premiumChat			= new AtomicBoolean(false);
	private final ChatFilters
			chatFilters			= ChatFilters.getInstance();

	CoreSettings() {}

	public boolean isPremiumChat() {
		return premiumChat.get();
	}

	public void setPremiumChat(boolean premiumChat) {
		this.premiumChat.set(premiumChat);
		RiddlesCore.broadcast("premiumchat." + (premiumChat ? "enable" : "disable"));
	}

	public void addChatFilter(ChatBlockFilter filter) {
		chatFilters.addFilter(filter);
	}

	public ChatFilters getChatFilters() {
		return chatFilters;
	}

	public void addAllowedCommands(List<String> commands) {
		allowedCommands.addAll(commands);
	}

	public void addAllowedCommand(String command) {
		allowedCommands.add(command.toLowerCase(Locale.ENGLISH));
	}

	public ImmutableList<String> getAllowedCommands() {
		return allowedCommands.getImmutableElements();
	}

	public void addLoginItem(IndexedItem item) {
		addLoginItem(item, player -> true);
	}

	public void addLoginItem(IndexedItem item, Predicate<CorePlayer> action) {
		loginItems.put(item, action);
	}

	public void clearLoginItems() {
		loginItems.clear();
	}

	public ImmutableMap<IndexedItem, Predicate<CorePlayer>> getLoginItems() {
		return loginItems.getImmutableEntries();
	}

	public void registerInventory(String path, CoreInventoryClickEvent event) {
		inventories.put(path, event);
	}

	public void registerInventories(Map<String, CoreInventoryClickEvent> events) {
		inventories.putAll(events);
	}

	public ImmutableMap<String, CoreInventoryClickEvent> getRegisteredInventories() {
		return inventories.getImmutableEntries();
	}

	public void registerCommands(JavaPlugin plugin, Map<String, CommandExecutor> commandExecutorMap) {
		for (Map.Entry<String, CommandExecutor> entry : commandExecutorMap.entrySet()) {
			plugin.getCommand(entry.getKey()).setExecutor(entry.getValue());
		}
	}

	public void registerListeners(JavaPlugin instance, Listener... listeners) {
		for (Listener listener : listeners) {
			instance.getServer().getPluginManager().registerEvents(listener, instance);
		}
	}

	public void findAndRegisterLocales(JavaPlugin plugin) {
		findAndRegisterLocales(new File(plugin.getDataFolder().getPath()
				+ File.separator
				+ "locale"
		));
	}

	public void findAndRegisterLocales(File directory) {
		if (!directory.exists() && !directory.mkdirs()) return;

		for (File file : directory.listFiles()) {
			String configName = file.getName();
			String locale = StringUtil.remove(configName, ".yml").toLowerCase();

			addLocale(locale);

			FileConfiguration config = new YamlConfiguration();

			try {
				config.load(file);
			} catch (IOException | InvalidConfigurationException e) {
				Messaging.log("Skipping loading %s: ");
				e.printStackTrace();
				continue;
			}

			for (String path : config.getKeys(true)) {
				String message = config.getString(path);
				addMessage(locale, path, message);
			}
		}
	}

	public String get(String path) {
		return get(getDefaultLocale(), path);
	}

	public String get(String locale, String path) {
		if (locale == null || StringUtils.isEmpty(path) || path.contains(" ")) {
			return path;
		}

		String message = messages.get(locale.toLowerCase() + "." + path);
		if (message == null) message = messages.get(getDefaultLocale() + "." + path);
		if (message == null || message.equalsIgnoreCase("null")) {
			return path.equals("chat.prefix") ? "" : path;
		}
		return message;
	}

	public void addMessage(String locale, String path, String message) {
		locale = locale.toLowerCase();

		if (!locales.contains(locale)) {
			locale = getDefaultLocale();
			addLocale(locale);
		}

		messages.put(
				locale + "." + path,
				StringUtils.isEmpty(message) || message.equalsIgnoreCase("null")
						? "null"
						: ChatColor.translateAlternateColorCodes('&', message)
		);
	}

	public void addLocale(String locale) {
		locales.addIf(
				Messaging.logIf(
						!Strings.isNullOrEmpty(locale) && !locales.contains(locale),
						"New locale detected: `%s`",
						WordUtils.capitalize(locale)
				), locale
		);
	}

	public ImmutableList<String> getLocales() {
		return locales.getImmutableElements();
	}

	public String getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale) {
		addLocale(this.defaultLocale = defaultLocale);
	}

	public ImmutableMap<String, String> getAllMessages() {
		return messages.getImmutableEntries();
	}

	public String getLocaleOrDefault(String locale) {
		return locales.contains(locale.toLowerCase()) ? locale : getDefaultLocale();
	}

	public void initClasses(String... classPaths) {
		for (String classPath : classPaths) {
			try {
				Class.forName(classPath);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void tryPasteLocales() {
		Bukkit.getScheduler().runTaskAsynchronously(RiddlesCore.getInstance(), () -> {
			StringBuilder sb = new StringBuilder();
			String oldLocale = null;
			for (Map.Entry<String, String> msg : RiddlesCore.getSettings().getAllMessages().entrySet()) {
				String[] parts = msg.getKey().split("\\.");
				String locale = parts[0];
				String path = StringUtils.join(ArrayUtils.subarray(parts, 1, parts.length + 1), ".");
				String message = RiddlesCore.getSettings().get(locale, path);

				if (message != null && !message.equalsIgnoreCase("null")) {
					if (message.startsWith("MemorySection")) continue;

					message = ChatColor.stripColor(message);
				}

				if (oldLocale == null || !locale.equals(oldLocale)) {
					oldLocale = locale;

					sb.append("\n").append(locale).append(":");
				}

				sb.append(String.format("\n\t\"%-48s => \"%s\"", path + "\"", message));
			}

			try {
				Paster.hastebin(sb.toString()).paste();
			} catch (PasteException e) {
				Messaging.log("Did not paste locale-supported messages: %s", e);
			}
		});
	}
}