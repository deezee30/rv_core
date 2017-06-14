package com.riddlesvillage.core.gun;

/**
 * Created by Matthew E on 6/14/2017.
 */
public abstract class FlameThrowerGun extends Gun {
    private double range;
    private int fireTicks;
    private String deathMessage;

    public FlameThrowerGun(String name) {
        super(name);
    }

    public double getRange() {
        return range;
    }

    public FlameThrowerGun withRange(double range) {
        this.range = range;
        return this;
    }

    public int getFireTicks() {
        return fireTicks;
    }

    public FlameThrowerGun withFireTicks(int fireTicks) {
        this.fireTicks = fireTicks;
        return this;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public FlameThrowerGun withDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
        return this;
    }
}
