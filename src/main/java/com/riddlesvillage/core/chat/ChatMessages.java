/*
 * rv_core
 * 
 * Created on 14 June 2017 at 11:06 PM.
 */

package com.riddlesvillage.core.chat;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.net.paster.PasteException;
import com.riddlesvillage.core.net.paster.Paster;

import java.net.URL;
import java.util.Collections;
import java.util.List;

public final class ChatMessages {

	private static final ChatMessages INSTANCE = new ChatMessages();

	private List<ChatMessage> chatMessageList = Collections.synchronizedList(Lists.newArrayList());

	// disable initialization
	private ChatMessages() {}

	public void pasteChatMessages() {
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonElements = new JsonArray();
		for (ChatMessage chatMessage : chatMessageList) {
			jsonElements.add(chatMessage.toJsonObject());
		}

		jsonObject.add("messages", jsonElements);
		try {
			URL paste = Paster.hastebin(jsonObject.toString()).paste();
			Messaging.log("Saved chat messages to %s", paste.toString());
		} catch (PasteException e) {
			e.printStackTrace();
		}
	}

	public void add(ChatMessage message) {
		chatMessageList.add(message);
	}

	public static ChatMessages getInstance() {
		return INSTANCE;
	}
}