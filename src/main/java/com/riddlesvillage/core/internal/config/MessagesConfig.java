/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.config;

import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.file.ConfigFile;
import com.riddlesvillage.core.internal.ConsoleOutput;
import com.riddlesvillage.core.net.paster.PasteException;
import com.riddlesvillage.core.net.paster.Paster;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.Map;

public final class MessagesConfig {

	private static final RiddlesCore PLUGIN_INSTANCE = RiddlesCore.getInstance();

	static {
		String defaultLocale = RiddlesCore.getSettings().getDefaultLocale();

		ConfigFile.check(
				new File(String.format(
						"%s%slocale",
						PLUGIN_INSTANCE.getDataFolder().getPath(),
						File.separator
				), defaultLocale + ".yml"),
				PLUGIN_INSTANCE.getResource("locale/" + defaultLocale + ".yml") // locale/english.yml
		);

		RiddlesCore.getSettings().findAndRegisterLocales(PLUGIN_INSTANCE);

		Messaging.setNoPrefixChar((char) 126);
		Messaging.setPrefix(ChatColor.stripColor(
				ChatColor.translateAlternateColorCodes('&', RiddlesCore.getSettings().get("chat.prefix"))
		));

		Messaging.setOutput(new ConsoleOutput(Bukkit.getConsoleSender()));

		// Print all locales in an organised fashion
		Bukkit.getScheduler().runTaskAsynchronously(PLUGIN_INSTANCE, () -> {
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

	private MessagesConfig() {}
}