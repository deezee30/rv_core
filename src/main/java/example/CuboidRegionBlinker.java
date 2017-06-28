/*
 * rv_core
 * 
 * Created on 28 June 2017 at 3:30 AM.
 */

package example;

import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.region.Region;
import com.riddlesvillage.core.world.region.RegionException;
import com.riddlesvillage.core.world.region.RegionManager;
import com.riddlesvillage.core.world.region.Regions;
import com.riddlesvillage.core.world.region.flag.Flag;
import com.riddlesvillage.core.world.region.type.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

final class CuboidRegionBlinker extends JavaPlugin {

    @Override
    public void onEnable() {
        // generate a cuboid region in world "world"
        // with specified min and max coordinates
        Region region = new CuboidRegion(
                "world",
                new Vector3D(-20, 50, -5),
                new Vector3D(15, 68, 10)
        );

        // add custom random flags
        region.addFlag(Flag.CHAT, false);
        region.addFlag(Flag.BUILD, false);
        region.addFlag(Flag.BREAK, true);

        // if such a region doesn't exist,
        // register region to support the flags
        // and also write them to file
        RegionManager manager = Regions.getManager();
        if (!manager.isRegistered(region)) {
            try {
                Regions.getManager().register(region, true);
            } catch (RegionException e) {
                e.printStackTrace();
            }
        }

        // every 4 seconds, blink the outline of the region with glass blocks.
        // each blink lasts 2 seconds. These are fake packets that don't
        // modify the world
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (CorePlayer player : CorePlayerManager.getInstance()) {
                region.showEdges(Material.GLASS, 40L, player);
            }
        }, 80L, 80L);
    }
}