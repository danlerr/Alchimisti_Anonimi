package alchgame.service;

import alchgame.model.game.AlchGame;
import alchgame.model.game.Round;
import alchgame.model.player.Player;

/**
 * Possiede il loop principale della partita e notifica il GameFlowHandler
 * ad ogni evento (inizio round, fase, fine round, game over).
 * Espone anche lo stato della risoluzione, unica fase con dati interrogabili
 * dall'esterno durante il suo svolgimento.
 */
public class GameSession {

    private final AlchGame alchGame;

    public GameSession(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    public void run(GameFlowHandler handler) {
        while (!alchGame.isOver()) {
            Round round = alchGame.getCurrentRound();
            handler.onRoundStart(alchGame.getCurrentRoundNumber(), alchGame.getTotalRounds());

            handler.onOrderPhase();
            round.tryAdvancePhase();
            handler.onDeclarationPhase();
            round.tryAdvancePhase();
            handler.onResolutionPhase();

            handler.onRoundEnd(alchGame.getCurrentRoundNumber());
            if (!alchGame.isOver()) alchGame.advanceRound();
        }
        handler.onGameOver(alchGame.getPlayers());
    }

    // --- Stato risoluzione (interrogato da ResolutionPhasePresenter durante onResolutionPhase) ---

    public boolean isResolutionComplete() {
        return alchGame.getCurrentRound().resolutionPhase().isComplete();
    }

    public String currentResolutionActionId() {
        return alchGame.getCurrentRound().resolutionPhase().currentActionId();
    }

    public Player currentResolutionPlayer() {
        return alchGame.getCurrentRound().resolutionPhase().getCurrentPlayer();
    }

    public int currentResolutionStepIndex() {
        return alchGame.getCurrentRound().resolutionPhase().currentStepIndex();
    }

    public int totalResolutionSteps() {
        return alchGame.getCurrentRound().resolutionPhase().totalSteps();
    }

    public void advanceResolution() {
        alchGame.getCurrentRound().resolutionPhase().advanceTurn();
    }
}
