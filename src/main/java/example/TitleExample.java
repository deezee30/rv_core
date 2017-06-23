package example;

import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.event.CorePlayerPostLoadEvent;
import com.riddlesvillage.core.title.Title;
import com.riddlesvillage.core.title.TitleBuilder;
import com.riddlesvillage.core.title.TitleMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TitleExample implements Listener {

    @EventHandler
    public void onPlayerJoin(CorePlayerPostLoadEvent event) {
        CorePlayer player = event.getPlayer();
		Title title = new TitleBuilder()
				.withTitle(new TitleMessage(TitleMessage.Type.TITLE)
						.withMessage("&8Welcome, " + player.getDisplayName())
						.after(40)
						.fadeInFor(40)
						.stayFor(80)
						.fadeOutFor(40)
				).withTitle(new TitleMessage(TitleMessage.Type.SUBTITLE)
						.withMessage("&7Enjoy your time on &6RiddlesVillage!")
						.after(60)
						.fadeInFor(20)
						.stayFor(40)
						.fadeOutFor(20)
				).build();

		title.send(player);
    }
}