package alchgame.service;

import alchgame.model.game.AlchGame;
import alchgame.model.player.Player;

import java.util.List;

/**
 * Espone alla presentation layer le operazioni di flusso automatico della partita:
 * avanzamento di fasi e round, stato della risoluzione, fine partita.
 */
public class GameSession {

    private final AlchGame alchGame;

    public GameSession(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    // --- Flusso round / fase ---

    public void tryAdvancePhase() {
        alchGame.getCurrentRound().tryAdvancePhase();
    }

    public void advanceRound() {
        alchGame.advanceRound();
    }

    // --- Stato partita ---

    public boolean isGameOver() {
        return alchGame.isOver();
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

    // --- Risoluzione ---

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
