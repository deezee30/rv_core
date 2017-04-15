package com.riddlesvillage.core.api;

import java.util.List;
import java.util.Random;

/**
 * Created by Matthew E on 4/1/2017.
 */
public class RandomAPI {

    private Random random;
    private static RandomAPI instance;

    public static RandomAPI getInstance() {
        if (instance == null) {
            instance = new RandomAPI();
        }
        return instance;
    }

    public RandomAPI() {
        instance = this;
        this.random = new Random();
    }

    public float random(float min, float max) {
        return random(this.random, min, max);
    }

    public int random(int min, int max) {
        return random(this.random, min, max);
    }

    public int random(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public float random(Random random, float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    public <T> T getRandomElementFromList(List<T> list) {
        if (!list.isEmpty()) {
            return list.get(random(0, list.size() - 1));
        } else {
            return null;
        }
    }

    public boolean getRandomChance(float chance) {
        return random.nextFloat() < chance;
    }
}
