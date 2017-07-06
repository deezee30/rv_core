package example.spleef.team;

import com.riddlesvillage.core.pgm.player.GamePlayer;
import com.riddlesvillage.core.pgm.player.GamePlayerList;
import com.riddlesvillage.core.pgm.team.ITeam;
import org.bukkit.ChatColor;

/**
 * Created by Matthew E on 7/6/2017.
 */
public class BlueTeam implements ITeam {
    private GamePlayerList gamePlayerList;

    public BlueTeam() {
        this.gamePlayerList = new GamePlayerList();
    }

    @Override
    public String getName() {
        return "blue";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.BLUE;
    }

    @Override
    public boolean addPlayer(GamePlayer player) {
        gamePlayerList.add(player);
        return true;
    }

    @Override
    public boolean removePlayer(GamePlayer player) {
        gamePlayerList.remove(player);
        return true;
    }

    @Override
    public GamePlayerList getPlayers() {
        return gamePlayerList;
    }

    @Override
    public boolean isFriendlyFire() {
        return false;
    }

    @Override
    public double getRelativeRating() {
        return 0;
    }
}
