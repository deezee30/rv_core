package example;

import com.riddlesvillage.core.gun.Gun;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;

/**
 * Created by Matthew E on 6/22/2017.
 */
class PistolGun extends Gun {
    public PistolGun() {
        super("pistol");
    }

    @Override
    public void fire(Player shooter) {
        Snowball snowball = shooter.launchProjectile(Snowball.class);
        shooter.setGlowing(true);
        snowball.setInvulnerable(true);
    }
}
