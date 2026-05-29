package alchgame.controller;

import alchgame.model.game.AlchGame;
import alchgame.model.player.Player;

import java.util.List;

public class GameFlowController {

    private final AlchGame alchGame;

    public GameFlowController(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    public void advanceRound() {
        alchGame.advanceRound();
    }

    public int getCurrentRoundNumber() {
        return alchGame.getCurrentRoundNumber();
    }

    public int getTotalRounds() {
        return alchGame.getTotalRounds();
    }

    public List<Player> getPlayers() {
        return alchGame.getPlayers();
    }

    public void advancePhase() {
        alchGame.getCurrentRound().advancePhase();
    }

    public boolean isGameOver() {
        return alchGame.isOver();
    }
}
