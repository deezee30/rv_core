package com.riddlesvillage.core.gun;

import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 6/14/2017.
 */
public class NormalFlameThrower extends FlameThrowerGun {
    public NormalFlameThrower() {
        super("normal");
    }

    @Override
    public void fire(Player shooter) {
        Fireball fireball = shooter.launchProjectile(Fireball.class);
        fireball.setGlowing(true);
        shooter.playSound(shooter.getLocation(), Sound.ENTITY_FIREWORK_BLAST, 1.0F, 1.0F);
        shooter.setVelocity(shooter.getLocation().getDirection().multiply(-2));
    }
}
