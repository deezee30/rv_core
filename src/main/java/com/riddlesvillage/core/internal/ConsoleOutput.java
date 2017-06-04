/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal;

import com.riddlesvillage.core.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.io.PrintStream;

public final class ConsoleOutput extends PrintStream {

	private final ConsoleCommandSender console;

	public ConsoleOutput(ConsoleCommandSender console) {
		super(Messaging.getOutput());
		this.console = console;
	}

	public ConsoleCommandSender getConsole() {
		return console;
	}

	@Override
	public void println() {
		print0(Messaging.getNoPrefixChar());
	}

	@Override
	public void println(String x) {
		print0(x);
	}

	@Override
	public void println(Object x) {
		print0(x);
	}

	private void print0(Object object) {
		console.sendMessage(ChatColor.translateAlternateColorCodes('&', object.toString()));
	}
}