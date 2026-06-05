package alchgame.presentation;

import alchgame.application.OrderController;
import alchgame.application.observer.GameStateDTO;
import alchgame.model.board.Board;
import alchgame.model.board.Resources;
import alchgame.model.player.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderPhasePresenter {

    private final OrderController orderController;
    private final Board board;
    private final GameView view;

    public OrderPhasePresenter(OrderController orderController, Board board, GameView view) {
        this.orderController = orderController;
        this.board = board;
        this.view = view;
    }

    public void showPhaseStart() {
        view.showPhaseHeader("Scelta dell'ordine nel tracciato di risveglio");
    }

    public void showPhaseEnd() {
        view.showWakeUpOrder(board.getWakeUpOrder().stream()
                .map(Player::getName).toList());
    }

    public void handleTurn(GameStateDTO state) {
        Player player = state.currentPlayer();

        view.showBoard(orderSlots(), board.getActionSpaceIds(), declarantsByAction());
        view.showCurrentPlayer(player.getName());
        view.showPlayerStatus(player.getGold(), player.getReputation(), player.getActionCubes());
        view.showIngredients(player.getIngredientsFromLab().stream()
                .map(i -> i.getName()).toList());

        while (true) {
            List<String> slots = orderController.getAvailableSlots();
            view.showAvailableSlots(slots);
            int choice = view.promptSlotChoice(slots.size());
            String slotId = slots.get(choice - 1);
            try {
                Resources res = orderController.chooseSlot(slotId);
                view.showSlotChoiceResult(slotId, res.ingredientCount(), res.favorCount());
                view.showFavors(player.getFavorCards().stream()
                        .map(f -> f.getName()).toList());
                break;
            } catch (Exception e) {
                view.showInvalidInput(e.getMessage());
            }
        }
    }

    private Map<String, String> orderSlots() {
        Map<String, String> result = new LinkedHashMap<>();
        board.getOrderAssignments().forEach((slot, player) ->
                result.put(slot, player != null ? player.getName() : null));
        return result;
    }

    private Map<String, List<String>> declarantsByAction() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String id : board.getActionSpaceIds())
            result.put(id, board.getActionSpace(id).getDeclaredPlayers().stream()
                    .map(Player::getName).toList());
        return result;
    }
}
