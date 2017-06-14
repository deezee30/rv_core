package example;

import com.riddlesvillage.core.title.TitleBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Matthew E on 6/14/2017.
 */
public class TitleExample implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TitleBuilder.createTitle()
                .withTitle(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Riddles Core")
                .withSubTitle(ChatColor.GRAY + "Welcome to riddles core test")
                .withFadeIn(10)
                .withFadeOut(10)
                .withDuration(20)
                .build()
                .send(player);
    }
}
