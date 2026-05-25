package alchgame.controller;

import alchgame.model.board.Resources;
import alchgame.model.game.Round;

import java.util.List;
import java.util.function.Supplier;

public class RoundController {

    private final Supplier<Round> round;

    public RoundController(Supplier<Round> turnSupplier) {
        this.round = turnSupplier;
    }

    public List<String> getAvailableSlots() {
        return round.get().orderPhase().getAvailableSlotIds();
    }

    public Resources chooseSlot(String orderSlotID) {
        Round r = round.get();
        return r.orderPhase().chooseSlot(r.getCurrentPlayer(), orderSlotID);
    }

    public List<String> getActionList() {
        return round.get().getBoard().getActionSpaceIds();
    }

    public void declareAction(String actionSpaceId) {
        Round r = round.get();
        r.declarationPhase().declareAction(r.getCurrentPlayer(), actionSpaceId);
    }
}
