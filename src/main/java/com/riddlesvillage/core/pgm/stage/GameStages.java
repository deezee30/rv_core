package com.riddlesvillage.core.pgm.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew E on 7/5/2017.
 */
public class GameStages {
    private Map<String, GameStage> gameModeStageMap;
    private String currentStageName;

    public GameStages() {
        this.gameModeStageMap = new HashMap<>();
    }

    public void add(GameStage gameModeStage) {
        if (!gameModeStageMap.containsKey(gameModeStage.getName())) {
            gameModeStageMap.put(gameModeStage.getName(), gameModeStage);
        }
    }

    public void remove(GameStage gameModeStage) {
        if (gameModeStageMap.containsKey(gameModeStage.getName())) {
            gameModeStageMap.remove(gameModeStage.getName());
        }
    }

    public List<GameStage> getGameModeStageList() {
        return new ArrayList<>(gameModeStageMap.values());
    }

    public GameStage getCurrentStage() {
        return gameModeStageMap.get(currentStageName);
    }

    public String getCurrentStageName() {
        return currentStageName;
    }

    public GameStages setCurrentStageName(String currentStageName) {
        this.currentStageName = currentStageName;
        getCurrentStage().start();
        return this;
    }
}
