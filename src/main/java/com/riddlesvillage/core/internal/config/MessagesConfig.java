/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.config;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.CoreSettings;
import com.riddlesvillage.core.Logger;
import com.riddlesvillage.core.file.ConfigFile;
import com.riddlesvillage.core.internal.ConsoleOutput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;

public final class MessagesConfig {

    private static final Core PLUGIN_INSTANCE = Core.get();
    private static final CoreSettings SETTINGS = Core.getSettings();

    static {
        String defaultLocale = SETTINGS.getDefaultLocale();

        ConfigFile.check(
                new File(String.format(
                        "%s%slocale",
                        PLUGIN_INSTANCE.getDataFolder().getPath(),
                        File.separator
                ), defaultLocale + ".yml"),
                PLUGIN_INSTANCE.getResource("locale/" + defaultLocale + ".yml") // locale/english.yml
        );

        SETTINGS.findAndRegisterLocales(PLUGIN_INSTANCE);

        Logger logger = Core.getCoreLogger();
        logger.setDebugPrefix("Core [Debug] -> ");
        logger.setNoPrefixChar((char) 126);
        logger.setPrefix(
                ChatColor.translateAlternateColorCodes('&', SETTINGS.get("chat.prefix"))
        );

        logger.setOutput(new ConsoleOutput(Bukkit.getConsoleSender()));

        SETTINGS.tryPasteLocales();
    }

    private MessagesConfig() {}
}