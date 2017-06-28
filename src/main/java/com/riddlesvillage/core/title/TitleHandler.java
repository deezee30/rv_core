/*
 * rv_core
 * 
 * Created on 21 June 2017 at 8:15 PM.
 */

package com.riddlesvillage.core.title;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.packet.wrapper.WrapperPlayServerTitle;
import com.riddlesvillage.core.player.CorePlayer;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class TitleHandler {

    TitleHandler() {}

    public void send(final WrapperPlayServerTitle packet,
                     final CorePlayer... players) {
        Validate.notNull(packet);
        for (CorePlayer player : players) {
            packet.sendPacket(player.getPlayer());
        }
    }

    public void send(final WrapperPlayServerTitle packet,
                     final Player... players) {
        Validate.notNull(packet);
        for (Player player : players) {
            packet.sendPacket(player);
        }
    }

    public WrapperPlayServerTitle buildClearPacket() {
        WrapperPlayServerTitle packet = new WrapperPlayServerTitle();

        packet.setAction(EnumWrappers.TitleAction.CLEAR);

        return packet;
    }

    public WrapperPlayServerTitle buildTimingPacket(final TitleMessage message) {
        Validate.notNull(message);
        WrapperPlayServerTitle packet = new WrapperPlayServerTitle();

        packet.setAction(EnumWrappers.TitleAction.TIMES);
        if (message.getStay().isPresent()) packet.setStay(message.getStay().get());
        if (message.getFadeIn().isPresent()) packet.setFadeIn(message.getFadeIn().get());
        if (message.getFadeOut().isPresent()) packet.setFadeOut(message.getFadeOut().get());

        return packet;
    }

    public WrapperPlayServerTitle buildTitlePacket(final CorePlayer player,
                                                   final TitleMessage message) {
        Validate.notNull(player);
        Validate.notNull(message);
        WrapperPlayServerTitle packet = new WrapperPlayServerTitle();

        packet.setAction(message.getType().equals(TitleMessage.Type.TITLE)
                ? EnumWrappers.TitleAction.TITLE : EnumWrappers.TitleAction.SUBTITLE);
        if (message.getMessage().isPresent()) {
            String locale = player.getLocale();
            String path = message.getMessage().get();
            String msg = Core.getSettings().get(locale, path);

            /*
             * Check if the path actually exists in the messages cache.
             * If not, block the message if it's a path or send it if it's not.
             */
            if (msg.equals(path)) {
                if (!path.contains(" ") && !path.equals(String.valueOf(Core.getCoreLogger().getNoPrefixChar()))) {
                    return packet;
                }
            }

            packet.setTitle(WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', msg)));
        }

        return packet;
    }

    public void handleTitleSendPacket(final TitleMessage title,
                                      final CorePlayer... players) {
        Validate.notNull(title);
        Bukkit.getScheduler().runTaskLater(Core.get(), () -> {
            boolean animated = title.isAnimated();

            for (CorePlayer player : players) {
                // send the timings first if they're animated
                if (animated && title.getType().equals(TitleMessage.Type.TITLE)) send(buildTimingPacket(title), player);

                send(buildTitlePacket(player, title), player);
            }
        }, title.isDelayed() ? title.getAfter().get() : 0L);
    }
}