package alchgame.service;

import alchgame.model.game.TurnPhase;
import alchgame.model.game.GameStatus;
import alchgame.model.game.GameSession;
import alchgame.model.game.TurnManager;
import alchgame.model.player.Player;

import java.util.List;

/**
 * Gestisce il flusso della partita: fasi del round, ordine dei giocatori e avanzamento round.
 */
public class GameFlowService {

    private final GameSession game;

    public GameFlowService(GameSession alchGame) {
        this.game = alchGame;
    }

    public List<Player> getOrderPhaseOrder() {
        return turnManager().getOrderPhaseOrder();
    }

    public List<Player> getDeclarationPhaseOrder() {
        TurnManager tm = turnManager();
        tm.advanceTo(TurnPhase.DECLARATION);
        return tm.getDeclarationPhaseOrder();
    }

    public List<Player> getResolutionOrderFor(String actionSpaceId) {
        TurnManager tm = turnManager();
        tm.advanceTo(TurnPhase.RESOLUTION);
        return tm.getResolutionOrderFor(actionSpaceId);
    }

    public void endRound() {
        requirePlaying();
        game.advanceRound();
    }

    public void endGame() {
        requirePlaying();
        game.end();
    }

    public boolean isPlaying()       { return game.isStarted(); }
    public int getCurrentRound()    { return game.getCurrentRound(); }
    public int getTotalRounds()     { return game.getTotalRounds(); }

    public TurnPhase getCurrentPhase() {
        if (!game.isStarted()) return null;
        return game.getTurnManager().getCurrentPhase();
    }

    private TurnManager turnManager() {
        requirePlaying();
        return game.getTurnManager();
    }

    private void requirePlaying() {
        if (game.getLifecycle() != GameStatus.PLAYING)
            throw new IllegalStateException("Operazione ammessa solo durante PLAYING.");
    }
}
