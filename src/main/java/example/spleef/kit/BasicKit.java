package example.spleef.kit;

import com.riddlesvillage.core.pgm.kit.IKit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Created by Matthew E on 7/6/2017.
 */
public class BasicKit implements IKit {
    @Override
    public String getName() {
        return "basic";
    }

    @Override
    public String getDescription() {
        return "The basic kit";
    }

    @Override
    public Color getColor() {
        return Color.AQUA;
    }

    @Override
    public int getHealth() {
        return 20;
    }

    @Override
    public Collection<ItemStack> getInventory() {
        return Arrays.asList(new ItemStack(Material.WOOD_SPADE), new ItemStack(Material.SNOW_BALL, 64));
    }

    @Override
    public Collection<ItemStack> getArmor() {
        return new ArrayList<>();
    }

    @Override
    public Collection<PotionEffect> getPotionEffects() {
        return Arrays.asList(new PotionEffect(PotionEffectType.SPEED, 1, 2344232, false, false));
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<ItemStack> iterator() {
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    @Override
    public String toJSONString() {
        return null;
    }
}
