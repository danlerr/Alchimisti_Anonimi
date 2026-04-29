package alchgame.service;

import alchgame.model.game.TurnPhaseType;
import alchgame.model.game.GameStatus;
import alchgame.model.game.GameSession;
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

    /** Ordine fase ordine: senso orario dal primo giocatore; paralizzati in coda consumando il flag. */
    public List<Player> getOrderPhaseOrder() {
        requirePlaying();
        return game.getOrderPhaseOrder();
    }

    /** Ordine dichiarazione: inverso wake-up (bottom -> top). */
    public List<Player> getDeclarationPhaseOrder() {
        requirePlaying();
        game.advanceTo(TurnPhaseType.DECLARATION);
        return game.getDeclarationPhaseOrder();
    }

    /**
     * Ordine risoluzione per uno spazio azione: wake-up (top -> bottom),
     * filtrato ai soli player che hanno dichiarato quell'azione.
     */
    public List<Player> getResolutionOrderFor(String actionSpaceId) {
        requirePlaying();
        game.advanceTo(TurnPhaseType.RESOLUTION);
        return game.getResolutionOrderFor(actionSpaceId);
    }

    public void endRound() {
        requirePlaying();
        game.advanceTo(TurnPhaseType.CLEANUP);
        game.endRound();
    }

    public void endGame() {
        requirePlaying();
        game.end();
    }

    public int getCurrentRound() { return game.getCurrentRound(); }
    public int getTotalRounds() { return game.getTotalRounds(); }
    public TurnPhaseType getCurrentPhase() { return game.getCurrentPhase(); }

    private void requirePlaying() {
        if (game.getLifecycle() != GameStatus.PLAYING)
            throw new IllegalStateException("Operazione ammessa solo durante PLAYING.");
    }
}
