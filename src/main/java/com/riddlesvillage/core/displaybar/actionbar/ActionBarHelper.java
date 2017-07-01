/*
 * rv_core
 * 
 * Created on 28 June 2017 at 8:17 PM.
 */

package com.riddlesvillage.core.displaybar.actionbar;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.packet.AbstractPacket;
import com.riddlesvillage.core.packet.wrapper.WrapperPlayServerChat;
import com.riddlesvillage.core.player.CorePlayer;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.WeakHashMap;

public class ActionBarHelper {

    // Default: Update action bar asynchronously 5 times a second
    public static final transient long UPDATE_RATE_TICKS = 4;

    private ActionBarHelper() {}

    public static Map<CorePlayer, AbstractPacket> getRespectiveTitles(final ActionBar bar,
                                                                      final CorePlayer... players) {
        Map<CorePlayer, AbstractPacket> respectiveTitles = new WeakHashMap<>(players.length);
        for (CorePlayer player : players) {
            if (!player.isOnline()) continue;

            WrapperPlayServerChat packet = new WrapperPlayServerChat();

            packet.setMessage(WrappedChatComponent.fromText(
                    ChatColor.translateAlternateColorCodes('&', Core.getSettings().get(
                            player.getLocale(),
                            bar.getText()
                    ))
            ));
            packet.setPosition((byte) 2);

            respectiveTitles.put(player, packet);
        }

        return respectiveTitles;
    }
}