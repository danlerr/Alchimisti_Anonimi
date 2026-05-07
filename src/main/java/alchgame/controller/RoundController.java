package alchgame.controller;

import alchgame.model.board.Resources;
import alchgame.model.game.Round;

import java.util.List;
import java.util.function.Supplier;

public class RoundController {

    private final Supplier<Round> round;
    private final List<String> actionOrder;

    public RoundController(Supplier<Round> turnSupplier, List<String> actionOrder) {
        this.round = turnSupplier;
        this.actionOrder = List.copyOf(actionOrder);
    }

    public List<String> getAvailableSlots() {
        return round.get().getAvailableSlotIds();
    }

    public Resources chooseSlot(String orderSlotID) {
        return round.get().chooseSlot(orderSlotID);
    }

    public List<String> getActionList() {
        return actionOrder;
    }

    public void declareAction(String actionSpaceId) {
        round.get().declareAction(actionSpaceId);
    }
}
