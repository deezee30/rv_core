package com.riddlesvillage.core.game.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew E on 7/5/2017.
 */
public class GameModeStages {
    private Map<String, GameModeStage> gameModeStageMap;
    private String currentStageName;

    public GameModeStages() {
        this.gameModeStageMap = new HashMap<>();
    }

    public void add(GameModeStage gameModeStage) {
        if (!gameModeStageMap.containsKey(gameModeStage.getName())) {
            gameModeStageMap.put(gameModeStage.getName(), gameModeStage);
        }
    }

    public void remove(GameModeStage gameModeStage) {
        if (gameModeStageMap.containsKey(gameModeStage.getName())) {
            gameModeStageMap.remove(gameModeStage.getName());
        }
    }

    public List<GameModeStage> getGameModeStageList() {
        return new ArrayList<>(gameModeStageMap.values());
    }

    public GameModeStage getCurrentStage() {
        return gameModeStageMap.get(currentStageName);
    }

    public String getCurrentStageName() {
        return currentStageName;
    }

    public GameModeStages setCurrentStageName(String currentStageName) {
        this.currentStageName = currentStageName;
        getCurrentStage().start();
        return this;
    }
}
