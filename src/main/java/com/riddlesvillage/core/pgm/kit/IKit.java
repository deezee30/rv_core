/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:47 PM.
 */

package com.riddlesvillage.core.pgm.kit;

import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONAware;

import java.io.Serializable;
import java.util.Collection;

public interface IKit extends
        Cloneable, Serializable, JSONAware,
        Iterable<ItemStack>, ConfigurationSerializable {

    String getName();

    String getDescription();

    Color getColor();

    int getHealth();

    Collection<ItemStack> getInventory();

    Collection<ItemStack> getArmor();

    Collection<PotionEffect> getPotionEffects();
}