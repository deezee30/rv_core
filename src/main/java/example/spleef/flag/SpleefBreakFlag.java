package example.spleef.flag;

import com.riddlesvillage.core.world.region.flag.IFlag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Matthew E on 7/6/2017.
 */
public class SpleefBreakFlag implements IFlag<BlockBreakEvent> {

    @Override
    public Class<BlockBreakEvent> getEvent() {
        return BlockBreakEvent.class;
    }

    @Override
    public Location getLocationOfAction(BlockBreakEvent event) {
        return event.getBlock().getLocation();
    }

    @Override
    public Optional<Predicate<BlockBreakEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.SNOW) {
            return Optional.empty();
        }
        return Optional.of("Deny");
    }
}