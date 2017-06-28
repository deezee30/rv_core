/*
 * rv_core
 * 
 * Created on 13 June 2017 at 11:40 PM.
 */

package example;

import com.google.common.collect.ImmutableList;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.ScoreboardHolder;
import com.riddlesvillage.core.scoreboard.IScoreboard;
import com.riddlesvillage.core.scoreboard.ScoreboardContainer;
import com.riddlesvillage.core.scoreboard.Scoreboards;
import com.riddlesvillage.core.scoreboard.ScrollerScoreboard;
import org.bukkit.entity.Player;

import java.util.Map;

class ScoreboardPlayer implements ScoreboardHolder {

    private final CorePlayer player;
    private IScoreboard scoreboard;
    private ScoreboardContainer container;

    public ScoreboardPlayer(CorePlayer player) {
        this.player = player;

        container = new BasicScoreboardContainer(player);

        // a scroller scoreboard - maximum width is 32 characters
        // and a space between two messages is 5 characters
        // keep this variable in cache so that it can be updated
        scoreboard = new ScrollerScoreboard(this, 32, 5);

        // register the new scoreboard and let it refresh 10 times a second
        Scoreboards.newScoreboard(scoreboard, 2L);
    }

    @Override
    public ScoreboardContainer getScoreboard() {
        return container;
    }

    @Override
    public Player getBukkitPlayer() {
        return player.getPlayer();
    }

    final class BasicScoreboardContainer implements ScoreboardContainer {

        private final CorePlayer player;

        public BasicScoreboardContainer(CorePlayer player) {
            this.player = player;
        }

        @Override
        public String getTitle() {
            return "Welcome, " + player.getName() + "!";
        }

        @Override
        public Map<String, Integer> getRows() {
            return Scoreboards.orderedRows(ImmutableList.<String>builder()

                    .add("")

                    .add("&6&l> Rank")
                    .add(player.getRank().getDisplayName())
                    .add(" ")

                    .add("&6&l> Coins")
                    .add("&a" + player.getCoins())
                    .add("  ")

                    .add("&6&l> Tokens")
                    .add("&a" + player.getTokens())
                    .add("   ")

                    .add("&6&l> Premium")
                    .add("&a" + player.isPremium())
                    .add("    ")

                    // this message is bigger than max width, so it will be scrolling
                    .add("This is a very long message to demonstrate the scroller!")
                    .build());
        }
    }
}