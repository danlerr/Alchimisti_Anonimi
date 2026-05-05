package alchgame.controller;


import alchgame.GameConfig;
import alchgame.model.board.Resources;
import alchgame.model.game.Round;

import java.util.List;
import java.util.function.Supplier;

public class TurnController {

    private final Supplier<Round> round;

    public TurnController(Supplier<Round> turnSupplier) {
        this.round = turnSupplier;
    }

    public List<String> getAvailableSlots(){
        return round.get().getAvailableSlotIds();
    }

    public Resources chooseSlot(String orderSlotID) {
        return round.get().chooseSlot(orderSlotID);
    }

    public List<String> getActionList(){
        return GameConfig.ACTION_ORDER;
    }

    public void declareAction(String actionSpaceId) {
        round.get().declareAction(actionSpaceId);
    }

    public void executeAction(String actionSpaceId){
        round.get().getResolutionOrderFor(actionSpaceId);
    }
}
