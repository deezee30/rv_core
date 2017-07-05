package example;

import com.riddlesvillage.core.game.GameMode;
import com.riddlesvillage.core.game.stage.GameModeStage;
import com.riddlesvillage.core.world.region.flag.Flag;

/**
 * Created by Matthew E on 7/5/2017.
 */
public class SpleefGameMode extends GameMode {
    public static final String LOBBY_STAGE = "lobby";
    public static final String COUNT_DOWN_STAGE = "countdown";
    public static final String INGAME_STAGE = "ingame";

    public SpleefGameMode() {
        super("spleef");
    }

    @Override
    public void initGameMode() {
        this.maxPlayers = 12;

        this.options.add(Flag.BLOCK_SPREAD);
        this.options.add(Flag.ANIMAL_SPAWN);
        this.options.add(Flag.BLOCK_BURN);
        this.options.add(Flag.BREAK);
        this.options.add(Flag.BUILD);
        this.options.add(Flag.EXP_SPAWN);
        this.options.add(Flag.MONSTER_SPAWN);
        this.options.add(Flag.SLEEP);
        this.options.add(Flag.VEHICLE_DESTROY);
        this.options.add(Flag.VEHICLE_PLACE);
        this.options.add(Flag.POTION_SPLASH);

        this.stages.add(new GameModeStage(LOBBY_STAGE, this) {

            private static final int LOBBY_COUNT_DOWN_TIME = 30;
            private double lobbyCountDownTime;
            private boolean isCountingDown;

            @Override
            public void onEnd() {
                this.isCountingDown = false;
                sendMessage("Lobby stage done");
                stages.setCurrentStageName(COUNT_DOWN_STAGE);
            }

            @Override
            public boolean tick() {
                if (isCountingDown) {
                    if (maxPlayers / 2 > playerList.size()) {
                        this.lobbyCountDownTime = LOBBY_COUNT_DOWN_TIME;
                        this.isCountingDown = false;
                    }
                    if (this.lobbyCountDownTime > 0) {
                        this.lobbyCountDownTime -= 0.25;
                    } else {
                        return true;
                    }
                    return false;
                }
                if (playerList.size() >= maxPlayers / 2) {
                    this.isCountingDown = true;
                    this.lobbyCountDownTime = LOBBY_COUNT_DOWN_TIME;
                }
                return false;
            }

            @Override
            public void onStart() {
                sendMessage("Lobby stage start");
            }
        });

        this.stages.add(new GameModeStage(COUNT_DOWN_STAGE, this) {

            private static final int COUNT_DOWN_TIME = 30;
            private double countDownTime;
            private boolean isCountingDown;

            @Override
            public void onEnd() {
                this.isCountingDown = false;
                stages.setCurrentStageName(COUNT_DOWN_STAGE);
                sendMessage("Countdown stage done");
            }

            @Override
            public boolean tick() {
                if (isCountingDown) {
                    if (maxPlayers / 2 > playerList.size()) {
                        this.countDownTime = COUNT_DOWN_TIME;
                        this.isCountingDown = false;
                    }
                    if (this.countDownTime > 0) {
                        this.countDownTime -= 0.25;
                    } else {
                        return true;
                    }
                    return false;
                }
                if (playerList.size() >= maxPlayers / 2) {
                    this.isCountingDown = true;
                    this.countDownTime = COUNT_DOWN_TIME;
                }
                return false;
            }

            @Override
            public void onStart() {
                sendMessage("Countdown stage start");
            }
        });
        this.stages.setCurrentStageName(LOBBY_STAGE);
    }
}
