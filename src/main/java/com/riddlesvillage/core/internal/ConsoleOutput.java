/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal;

import com.riddlesvillage.core.Core;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.io.PrintStream;

public final class ConsoleOutput extends PrintStream {

    private final ConsoleCommandSender console;

    public ConsoleOutput(final ConsoleCommandSender console) {
        super(Core.getCoreLogger().getOutput());
        this.console = Validate.notNull(console);
    }

    public ConsoleCommandSender getConsole() {
        return console;
    }

    @Override
    public void println() {
        print0(Core.getCoreLogger().getNoPrefixChar());
    }

    @Override
    public void println(final String x) {
        print0(x);
    }

    @Override
    public void println(final Object x) {
        print0(x);
    }

    private void print0(final Object object) {
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', object.toString()));
    }
}