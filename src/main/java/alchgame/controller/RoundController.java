package alchgame.controller;

import alchgame.model.board.Resources;
import alchgame.model.game.Round;

import java.util.List;
import java.util.function.Supplier;

public class RoundController {

    private final Supplier<Round> round;
    private final List<String> actionList;

    public RoundController(Supplier<Round> turnSupplier, List<String> actionList) {
        this.round = turnSupplier;
        this.actionList = List.copyOf(actionList);
    }

    public List<String> getAvailableSlots() {
        return round.get().orderPhase().getAvailableSlotIds();
    }

    public Resources chooseSlot(String orderSlotID) {
        Round r = round.get();
        return r.orderPhase().chooseSlot(r.getCurrentPlayer(), orderSlotID);
    }

    public List<String> getActionList() {
        return actionList;
    }

    public void declareAction(String actionSpaceId) {
        Round r = round.get();
        r.declarationPhase().declareAction(r.getCurrentPlayer(), actionSpaceId);
    }
}
