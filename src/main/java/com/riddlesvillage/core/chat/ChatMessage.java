/*
 * rv_core
 * 
 * Created on 14 June 2017 at 11:00 PM.
 */

package com.riddlesvillage.core.chat;

import com.google.gson.JsonObject;
import com.riddlesvillage.core.player.CorePlayer;
import org.json.simple.JSONAware;

import java.io.Serializable;
import java.util.UUID;

public class ChatMessage implements Serializable, JSONAware {

	private final CorePlayer sender;
	private final String message;
	private final long date;
	private final boolean cancelled;

	public ChatMessage(CorePlayer sender, String message, boolean cancelled) {
		this.sender = sender;
		this.message = message;
		this.date = System.currentTimeMillis();
		this.cancelled = cancelled;
	}

	public JsonObject toJsonObject() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("sender", sender.getUuid().toString());
		jsonObject.addProperty("message", message);
		jsonObject.addProperty("date", date);
		jsonObject.addProperty("cancelled", cancelled);

		JsonObject object = new JsonObject();
		object.add(UUID.randomUUID().toString(), jsonObject);

		return object;
	}

	@Override
	public String toJSONString() {
		return toJsonObject().toString();
	}
}