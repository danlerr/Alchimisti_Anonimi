package alchgame.controller;

import alchgame.GameConfig;
import alchgame.model.board.Resources;
import alchgame.model.game.TurnManager;
import alchgame.model.game.TurnPhase;

import java.util.List;
import java.util.function.Supplier;

public class TurnController {

    private final Supplier<TurnManager> turnManagerSupplier;

    public TurnController(Supplier<TurnManager> turnManagerSupplier) {
        this.turnManagerSupplier = turnManagerSupplier;
    }

    // ---- query: order phase -------------------------------------------------

    public List<String> getOrderPhasePlayerOrder() {
        return turnManagerSupplier.get().getOrderPhaseOrder().stream()
                .map(p -> p.getName())
                .toList();
    }

    public List<String> getAvailableSlotIds() {
        return turnManagerSupplier.get().getAvailableSlotIds();
    }

    public List<String> getWakeUpOrder() {
        return turnManagerSupplier.get().getWakeUpOrderNames();
    }

    // ---- query: declaration phase -------------------------------------------

    public List<String> getDeclarationPhaseOrder() {
        TurnManager tm = turnManagerSupplier.get();
        tm.advanceTo(TurnPhase.DECLARATION);
        return tm.getDeclarationPhaseOrder().stream()
                .map(p -> p.getName())
                .toList();
    }

    public int getCurrentPlayerActionCubes() {
        return turnManagerSupplier.get().getCurrentPlayer().getActionCubes();
    }

    public List<String> getActionOptions() {
        return GameConfig.ACTION_ORDER;
    }

    // ---- query: resolution phase --------------------------------------------

    public List<String> getResolutionOrderFor(String actionId) {
        TurnManager tm = turnManagerSupplier.get();
        tm.advanceTo(TurnPhase.RESOLUTION);
        return tm.getResolutionOrderFor(actionId).stream()
                .map(p -> p.getName())
                .toList();
    }

    public String getCurrentPlayerName() {
        return turnManagerSupplier.get().getCurrentPlayer().getName();
    }

    // ---- commands -----------------------------------------------------------

    public void setCurrentPlayerByName(String name) {
        turnManagerSupplier.get().setCurrentPlayerByName(name);
    }

    public Resources chooseSlot(String orderSlotID) {
        return turnManagerSupplier.get().chooseSlot(orderSlotID);
    }

    public void declareAction(String actionSpaceId) {
        turnManagerSupplier.get().declareAction(actionSpaceId);
    }
}
