package alchgame.controller;

import alchgame.model.board.Resources;
import alchgame.model.game.AlchGame;
import alchgame.model.player.Player;

import java.util.List;
import java.util.Map;

/**
 * Controller del caso d'uso "scegli la posizione nel tracciato di risveglio" (UC01).
 */
public class OrderController {

    private final AlchGame alchGame;

    public OrderController(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    public List<Player> getTurnOrder() {
        return alchGame.getCurrentRound().orderPhase().getTurnOrder();
    }

    public boolean isPhaseComplete() {
        return alchGame.getCurrentRound().orderPhase().isComplete();
    }

    public Player getCurrentPlayer() {
        return alchGame.getCurrentRound().orderPhase().getCurrentPlayer();
    }

    public void advanceTurn() {
        alchGame.getCurrentRound().orderPhase().advanceTurn();
    }

    public List<String> getAvailableSlots() {
        return alchGame.getCurrentRound().orderPhase().getAvailableSlotIds();
    }

    public Resources chooseSlot(String orderSlotId) {
        return alchGame.getCurrentRound().orderPhase().chooseSlot(orderSlotId);
    }

    public Map<String, Player> getOrderAssignments() {
        return alchGame.getCurrentRound().getBoard().getOrderAssignments();
    }

    public List<String> getActionList() {
        return alchGame.getCurrentRound().getBoard().getActionSpaceIds();
    }

    public List<Player> getDeclaredPlayers(String actionId) {
        return alchGame.getCurrentRound().getBoard().getActionSpace(actionId).getDeclaredPlayers();
    }

    public List<Player> getWakeUpOrder() {
        return alchGame.getCurrentRound().getBoard().getWakeUpOrder();
    }
}
