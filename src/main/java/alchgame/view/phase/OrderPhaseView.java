package alchgame.view.phase;

import alchgame.GameConfig;
import alchgame.controller.TurnController;
import alchgame.model.board.Resources;
import alchgame.view.GameView;
import alchgame.view.viewmodel.GameViewModels;
import alchgame.view.viewmodel.OrderSlotView;

import java.util.List;

public class OrderPhaseView {

    private final GameView view;

    public OrderPhaseView(GameView view) {
        this.view = view;
    }

    public void run(TurnController tc) {
        view.printSection("FASE ORDINE DI RISVEGLIO");
        for (String playerName : tc.getOrderPhasePlayerOrder()) {
            tc.setCurrentPlayerByName(playerName);
            view.showOrderTurn(playerName);
            List<String> freeIds = tc.getAvailableSlotIds();
            List<OrderSlotView> slots = GameViewModels.orderSlots(freeIds, GameConfig.SLOTS);
            String slotId = view.askSlotChoice(slots);
            Resources res = tc.chooseSlot(slotId);
            view.showSlotAssigned(GameViewModels.slotAssignment(playerName, slotId, res));
        }
        view.showWakeUpOrder(tc.getWakeUpOrder());
    }
}
