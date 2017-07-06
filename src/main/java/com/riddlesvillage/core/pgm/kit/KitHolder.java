/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:48 PM.
 */

package com.riddlesvillage.core.pgm.kit;

import com.riddlesvillage.core.player.profile.CoreProfile;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Optional;

public interface KitHolder extends CoreProfile {

    Optional<IKit> getCurrentKit();

    void equipKit(IKit kit);

    Collection<ItemStack> getInventory();

    Collection<ItemStack> getArmor();

    @Override
    default boolean isOnline() {
        return true;
    }
}