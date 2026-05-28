package alchgame.controller;

import alchgame.model.game.AlchGame;
import alchgame.model.game.Round;
import alchgame.model.player.Player;

import java.util.List;

public class GameFlowController {

    private final AlchGame alchGame;

    public GameFlowController(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    // --- Lifecycle ---

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

    // --- Phase ---

    public void advancePhase() {
        alchGame.getCurrentRound().advancePhase();
    }

    public void setCurrentPlayer(Player player) {
        alchGame.getCurrentRound().setCurrentPlayer(player);
    }

    // --- ORDER phase (call only during ORDER) ---

    public List<Player> getOrderPhaseOrder() {
        Round r = alchGame.getCurrentRound();
        return r.orderPhase().getPhaseOrder(alchGame.getPlayers());
    }

    // --- DECLARATION phase (call only during DECLARATION) ---

    public List<Player> getDeclarationPhaseOrder() {
        Round r = alchGame.getCurrentRound();
        return r.declarationPhase().getPhaseOrder(alchGame.getPlayers());
    }

    // --- RESOLUTION phase ---

    public boolean isResolutionComplete() {
        return alchGame.getCurrentRound().resolutionPhase().isComplete();
    }

    public String currentResolutionActionId() {
        return alchGame.getCurrentRound().resolutionPhase().currentActionId();
    }

    public Player currentResolutionPlayer() {
        return alchGame.getCurrentRound().resolutionPhase().currentPlayer();
    }

    public int getResolutionStepIndex() {
        return alchGame.getCurrentRound().resolutionPhase().currentStepIndex();
    }

    public int getResolutionTotalSteps() {
        return alchGame.getCurrentRound().resolutionPhase().totalSteps();
    }

    public void markCurrentPlayerResolved() {
        alchGame.getCurrentRound().resolutionPhase().markCurrentPlayerResolved();
    }

    public boolean isGameOver() {
        return alchGame.isOver();
    }

    public List<Player> getWakeUpOrder() {
        return alchGame.getCurrentRound().getBoard().getWakeUpOrder();
    }
}
