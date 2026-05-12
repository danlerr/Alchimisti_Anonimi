package alchgame.controller;

import alchgame.model.game.AlchGame;
import alchgame.model.game.Round;
import alchgame.model.game.phase.RoundPhase;
import alchgame.model.player.Player;

import java.util.List;

public class GameFlowController {

    private final AlchGame alchGame;

    public GameFlowController(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    // --- Lifecycle ---

    public boolean isGameOver() {
        return alchGame.isOver();
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

    // --- Phase ---

    public RoundPhase getCurrentPhase() {
        return alchGame.getCurrentRound().getCurrentPhase();
    }

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

    public void markCurrentPlayerResolved() {
        alchGame.getCurrentRound().resolutionPhase().markCurrentPlayerResolved();
    }
}
