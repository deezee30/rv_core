/*
 * rv_core
 * 
 * Created on 15 July 2017 at 2:53 PM.
 */

package com.riddlesvillage.core.net.communication.command;

import com.riddlesvillage.core.collect.EnhancedMap;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.Optional;

public final class CommandRegistry {

    private final EnhancedMap<Command, CommandProcess> commands = new EnhancedMap<>();

    public CommandProcess add(final Command command,
                              final CommandProcess process) {
        Validate.notNull(command);
        Validate.notNull(process);
        return commands.put(command, process);
    }

    public Optional<Command> getCommand(final String command) {
        Validate.notNull(command);
        for (Map.Entry<Command, CommandProcess> entry : commands.entrySet()) {
            Command cmd = entry.getKey();
            if (cmd.getName().equalsIgnoreCase(command)) {
                return Optional.of(cmd);
            }
        }

        return Optional.<Command>empty();
    }

    public Optional<CommandProcess> getProcess(final String command) {
        Optional<Command> cmd = getCommand(command);
        if (!cmd.isPresent()) return Optional.<CommandProcess>empty();
        return getProcess(cmd.get());
    }

    public Optional<CommandProcess> getProcess(final Command command) {
        Validate.notNull(command);
        return Optional.ofNullable(commands.get(command));
    }

    public boolean isRegistered(final String command) {
        return getCommand(command).isPresent();
    }
}