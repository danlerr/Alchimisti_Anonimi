package alchgame.controller;

import alchgame.model.game.AlchGame;
import alchgame.model.player.Player;

import java.util.List;
import java.util.Map;

/**
 * Controller del caso d'uso "dichiara azioni" (UC02).
 */
public class DeclarationController {

    private final AlchGame alchGame;

    public DeclarationController(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    public List<Player> getTurnOrder() {
        return alchGame.getCurrentRound().declarationPhase().getTurnOrder();
    }

    public boolean isPhaseComplete() {
        return alchGame.getCurrentRound().declarationPhase().isComplete();
    }

    public Player getCurrentPlayer() {
        return alchGame.getCurrentRound().declarationPhase().getCurrentPlayer();
    }

    public void advanceTurn() {
        alchGame.getCurrentRound().declarationPhase().advanceTurn();
    }

    public List<String> getActionList() {
        return alchGame.getCurrentRound().getBoard().getActionSpaceIds();
    }

    public boolean canCurrentPlayerDeclare() {
        return alchGame.getCurrentRound().declarationPhase().getCurrentPlayer().getActionCubes() > 0;
    }

    public void declareAction(String actionSpaceId) {
        alchGame.getCurrentRound().declarationPhase().declareAction(actionSpaceId);
    }

    public Map<String, Player> getOrderAssignments() {
        return alchGame.getCurrentRound().getBoard().getOrderAssignments();
    }

    public List<Player> getDeclaredPlayers(String actionId) {
        return alchGame.getCurrentRound().getBoard().getActionSpace(actionId).getDeclaredPlayers();
    }
}
