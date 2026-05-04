package alchgame.controller;


import alchgame.model.board.Resources;
import alchgame.model.game.Turn;


import java.util.function.Supplier;

public class TurnController {

    private final Supplier<Turn> turn;

    public TurnController(Supplier<Turn> turnSupplier) {
        this.turn = turnSupplier;
    }

    public Resources chooseSlot(String orderSlotID) {
        return turn.get().chooseSlot(orderSlotID);
    }

    public void declareAction(String actionSpaceId) {
        turn.get().declareAction(actionSpaceId);
    }
}
