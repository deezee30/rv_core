/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.config;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.CoreSettings;
import com.riddlesvillage.core.Logger;
import com.riddlesvillage.core.internal.ConsoleOutput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class MessagesConfig {

    private static final Core PLUGIN_INSTANCE = Core.get();
    private static final CoreSettings SETTINGS = Core.getSettings();

    static {
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