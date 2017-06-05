/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.config;

import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.RiddlesCore;

import java.util.List;

public final class MainConfig extends CoreConfigFile {

	private static final MainConfig INSTANCE = new MainConfig();

	public boolean debug;
	public String defaultLocale;
	public boolean showRankInChat;
	public int clearChatLines;
	public List<String> allowedCmds;

	private MainConfig() {
		allowedCmds.forEach(cmd -> RiddlesCore.getSettings().addAllowedCommand(cmd));

		Messaging.enableDebugging(debug);
		Messaging.setNoPrefixChar((char) 126);
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
				"show-rank-in-chat",
				"clearchat-lines",
				"allowed-commands-when-disabled"
		};
	}

	public static boolean isDebug() {
		return INSTANCE.debug;
	}

	public static String getDefaultLocale() {
		return INSTANCE.defaultLocale;
	}

	public static boolean doShowRankInChat() {
		return INSTANCE.showRankInChat;
	}

	public static int getClearChatLines() {
		return INSTANCE.clearChatLines;
	}
}