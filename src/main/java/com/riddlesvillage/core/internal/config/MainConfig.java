/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.config;

import com.riddlesvillage.core.Core;

import java.util.List;

public final class MainConfig extends CoreConfigFile {

    private static final MainConfig INSTANCE = new MainConfig();

    public boolean debug;
    public String defaultLocale;
    public boolean chatFormat;
    public int chatClearLines;
    public int maxMessages;
    public int chatSpamViolationsPermitted;
    public int chatViolationCooldown;
    public List<String> allowedCmds;

    private MainConfig() {
        Core.getCoreLogger().enableDebugging(debug);
    }

    @Override
    protected String getConfigName() {
        return "config.yml";
    }

    @Override
    protected String[] getPaths() {
        return new String[] {
                "debug",
                "default-locale",

                "chat.format",
                "chat.clear-lines",
                "chat.max-messages" ,
                "chat.spam-violations-permitted",
                "chat.violation-cooldown",

                "allowed-commands-when-disabled"
        };
    }

    public static boolean isDebug() {
        return INSTANCE.debug;
    }

    public static String getDefaultLocale() {
        return INSTANCE.defaultLocale;
    }

    public static boolean doFormatChat() {
        return INSTANCE.chatFormat;
    }

    public static int getClearChatLines() {
        return INSTANCE.chatClearLines;
    }

    public static int getMaxMessages() {
        return INSTANCE.maxMessages;
    }

    public static int getChatSpamViolationsPermitted() {
        return INSTANCE.chatSpamViolationsPermitted;
    }

    public static int getChatViolationCooldown() {
        return INSTANCE.chatViolationCooldown;
    }

    public static List<String> getAllowedCommands() {
        return INSTANCE.allowedCmds;
    }
}