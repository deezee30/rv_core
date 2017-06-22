package com.riddlesvillage.core.gun;

import com.riddlesvillage.core.gun.events.PlayerRightClickItemEvent;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew E on 6/22/2017.
 */
public class GunManager implements Listener {
    private static GunManager instance;
    private Map<String, Gun> gunMap;

    public static GunManager getInstance() {
        if (instance == null) {
            instance = new GunManager();
        }
        return instance;
    }

    public GunManager() {
        instance = this;
        this.gunMap = new HashMap<>();
    }

    public void registerGun(Gun gun) {
        gunMap.put(gun.getName(), gun);
    }

    @EventHandler
    public void onPlayerRightClickItem(PlayerRightClickItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        net.minecraft.server.v1_11_R1.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tagCompound = (stack.hasTag()) ? stack.getTag() : new NBTTagCompound();
        if (tagCompound.hasKey("gun")) {
            String gunName = tagCompound.getString("gunName");
            if (gunMap.containsKey(gunName)) {
                event.setCancelled(true);
                gunMap.get(gunName).fire(player);
                return;
            }
        }
    }
}
