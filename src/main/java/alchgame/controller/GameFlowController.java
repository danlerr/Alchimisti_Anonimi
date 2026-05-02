package alchgame.controller;

import alchgame.GameConfig;
import alchgame.service.GameFlowService;

import java.util.List;

public class GameFlowController {

    private final GameFlowService gameFlowService;

    public GameFlowController(GameFlowService gameFlowService) {
        this.gameFlowService = gameFlowService;
    }

    public boolean isGameOver()    { return !gameFlowService.isPlaying(); }
    public int getCurrentRound()   { return gameFlowService.getCurrentRound(); }
    public int getTotalRounds()    { return gameFlowService.getTotalRounds(); }

    public List<String> getResolutionOrder() {
        return GameConfig.RESOLUTION_ORDER;
    }

    public void endRound() {
        gameFlowService.endRound();
    }
}
