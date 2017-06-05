/*
 * RiddlesCore
 */

package com.riddlesvillage.core;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.util.StringUtil;
import com.riddlesvillage.core.util.inventory.item.IndexedItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public final class CoreSettings {

	public static final String DEFAULT_LOCALE = "english";

	private final EnhancedMap<String, String>
			messages			= new EnhancedMap<>();
	private final EnhancedMap<IndexedItem, Predicate<CorePlayer>>
			loginItems			= new EnhancedMap<>();
	private final EnhancedList<String>
			allowedCommands		= new EnhancedList<>(),
			locales				= new EnhancedList<>();
	private final EnhancedMap<String, CoreInventoryClickEvent>
			inventories = new EnhancedMap<>();
	private final AtomicBoolean
			premiumChat			= new AtomicBoolean(false);

	CoreSettings() {}

	public boolean isPremiumChat() {
		return premiumChat.get();
	}

	public void setPremiumChat(boolean premiumChat) {
		this.premiumChat.set(premiumChat);
		RiddlesCore.broadcast("premiumchat." + (premiumChat ? "enable" : "disable"));
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
		return get(DEFAULT_LOCALE, path);
	}

	public String get(String locale, String path) {
		if (locale == null || StringUtils.isEmpty(path) || path.contains(" ")) {
			return path;
		}

		String message = messages.get(locale.toLowerCase() + "." + path);
		if (message == null) message = messages.get(DEFAULT_LOCALE + "." + path);
		if (message == null || message.equalsIgnoreCase("null")) {
			return path.equals("chat.prefix") ? "" : path;
		}
		return message;
	}

	public void addMessage(String locale, String path, String message) {
		locale = locale.toLowerCase();

		if (!locales.contains(locale)) {
			locale = DEFAULT_LOCALE;
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

	public ImmutableMap<String, String> getAllMessages() {
		return messages.getImmutableEntries();
	}

	public String getLocaleOrDefault(String locale) {
		return locales.contains(locale.toLowerCase()) ? locale : DEFAULT_LOCALE;
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
}