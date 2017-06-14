package com.riddlesvillage.core.gun;

/**
 * Created by Matthew E on 6/14/2017.
 */
public abstract class AutomaticGun extends Gun {
    private int rps;

    public AutomaticGun(String name) {
        super(name);
    }

    public AutomaticGun withRps(int rps) {
        this.rps = rps;
        return this;
    }

    public int getRps() {
        return rps;
    }
}
