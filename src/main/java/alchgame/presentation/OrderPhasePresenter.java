package alchgame.presentation;

import alchgame.application.OrderController;
import alchgame.model.board.Resources;
import alchgame.model.player.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderPhasePresenter {

    private final OrderController orderController;
    private final GameView view;

    public OrderPhasePresenter(OrderController orderController, GameView view) {
        this.orderController = orderController;
        this.view = view;
    }

    public void run() {
        view.showPhaseHeader("Scelta dell'ordine nel tracciato di risveglio");
        view.showBoard(orderController.getOrderAssignments(),
                orderController.getActionList(),
                declarantsByAction(orderController.getActionList()));

        List<Player> order = orderController.getOrder();
        view.showPhaseOrder("di scelta della posizione nel tracciato di risveglio",
                order.stream().map(Player::getName).toList());

        while (!orderController.isPhaseComplete()) {
            Player player = orderController.getCurrentPlayer();
            view.showCurrentPlayer(player.getName());
            view.showPlayerStatus(player.getGold(), player.getReputation(), player.getActionCubes());
            view.showIngredients(player.getIngredientsFromLab());
            view.showOrderAssignments(orderController.getOrderAssignments());

            while (true) {
                List<String> slots = orderController.getAvailableSlots();
                view.showAvailableSlots(slots);
                int choice = view.promptSlotChoice(slots.size());
                String slotId = slots.get(choice - 1);
                try {
                    Resources res = orderController.chooseSlot(slotId);
                    view.showSlotChoiceResult(slotId, res);
                    view.showIngredients(player.getIngredientsFromLab());
                    view.showFavors(player.getFavorCards());
                    break;
                } catch (Exception e) {
                    view.showInvalidInput(e.getMessage());
                }
            }

            orderController.advanceTurn();
        }

        view.showWakeUpOrder(orderController.getWakeUpOrder().stream().map(Player::getName).toList());
    }

    private Map<String, List<String>> declarantsByAction(List<String> actionIds) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String id : actionIds) {
            result.put(id, orderController.getDeclaredPlayers(id).stream()
                    .map(Player::getName).toList());
        }
        return result;
    }
}
