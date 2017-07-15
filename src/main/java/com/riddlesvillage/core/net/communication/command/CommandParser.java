/*
 * rv_core
 * 
 * Created on 15 July 2017 at 3:02 PM.
 */

package com.riddlesvillage.core.net.communication.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.riddlesvillage.core.Core;
import org.apache.commons.lang3.Validate;

import java.util.Optional;

public final class CommandParser {

    public static Command parse(final String json) throws CommandException {
        return parse((JsonObject) new JsonParser().parse(Validate.notNull(json)));
    }

    public static Command parse(final JsonObject json) throws CommandException{
        try {
            Validate.notNull(json);
            String name = json.get("name").getAsString();
            String commandType = json.get("type").getAsString();
            JsonElement data = json.get("data");
            return new Command(name, CommandType.of(commandType), Optional.ofNullable(data));
        } catch (JsonParseException ex) {
            Core.log(json.toString());
            throw new CommandException("Received command that can't be parsed into a command", ex);
        }
    }
}