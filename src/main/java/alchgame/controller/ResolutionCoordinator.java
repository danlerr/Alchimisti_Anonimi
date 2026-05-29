package alchgame.controller;

import alchgame.model.game.AlchGame;
import alchgame.model.player.Player;

/**
 * Coordina la coda della fase di risoluzione: scorre gli step dichiarati e
 * delega l'esecuzione ai casi d'uso azione (Forage / Transmute / Experiment).
 * Non è un caso d'uso, bensì un orchestratore di coda — da qui il nome
 * {@code Coordinator} anziché {@code Controller}.
 */
public class ResolutionCoordinator {

    private final AlchGame alchGame;

    public ResolutionCoordinator(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    public boolean isComplete() {
        return alchGame.getCurrentRound().resolutionPhase().isComplete();
    }

    public String currentActionId() {
        return alchGame.getCurrentRound().resolutionPhase().currentActionId();
    }

    public Player currentPlayer() {
        return alchGame.getCurrentRound().resolutionPhase().currentPlayer();
    }

    public int currentStepIndex() {
        return alchGame.getCurrentRound().resolutionPhase().currentStepIndex();
    }

    public int totalSteps() {
        return alchGame.getCurrentRound().resolutionPhase().totalSteps();
    }

    public void setCurrentPlayer(Player player) {
        alchGame.getCurrentRound().setCurrentPlayer(player);
    }

    public void advance() {
        alchGame.getCurrentRound().resolutionPhase().markCurrentPlayerResolved();
    }
}
