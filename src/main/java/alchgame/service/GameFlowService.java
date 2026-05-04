package alchgame.service;

import alchgame.model.game.AlchGame;
import alchgame.model.game.GameStatus;

public class GameFlowService {

    private final AlchGame game;

    public GameFlowService(AlchGame alchGame) {
        this.game = alchGame;
    }

    public void endRound() {
        requirePlaying();
        game.advanceRound();
    }

    public void endGame() {
        requirePlaying();
        game.endGame();
    }

    public boolean isPlaying()    { return game.isStarted(); }
    public int getCurrentRound()  { return game.getCurrentRound(); }
    public int getTotalRounds()   { return game.getTotalRounds(); }

    private void requirePlaying() {
        if (game.getStatus() != GameStatus.PLAYING)
            throw new IllegalStateException("Operazione ammessa solo durante PLAYING.");
    }
}
