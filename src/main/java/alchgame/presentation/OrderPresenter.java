package alchgame.presentation;

import alchgame.application.OrderController;
import alchgame.application.dto.BoardStateDTO;
import alchgame.application.dto.PlayerDTO;
import alchgame.application.dto.SlotResultDTO;
import alchgame.application.dto.GameStateDTO;

import java.util.List;

public class OrderPresenter {

    private final OrderController orderController;
    private final GameView view;

    public OrderPresenter(OrderController orderController,
                               GameView view) {
        this.orderController = orderController;
        this.view = view;
    }

    public void showPhaseStart() {
        view.showPhaseHeader("Scelta dell'ordine nel tracciato di risveglio");
    }

    public void showPhaseEnd(GameStateDTO state) {
        view.showWakeUpOrder(state.boardState().wakeUpOrder());
    }

    public void handleTurn(GameStateDTO state) {
        view.clearScreen();
        PlayerDTO player = state.currentPlayer();
        BoardStateDTO board = state.boardState();

        view.showBoard(board.orderSlots(), board.wakeUpOrder(), board.actionIds(), board.declarantsByAction());
        view.showPublicBoards(state.publicBoards());
        view.showCurrentPlayer(player.name());
        view.showPlayerStatus(player.gold(), player.reputation(), player.actionCubes());
        view.showIngredients(player.ingredients().stream()
                .map(i -> i.name()).toList());
        view.showFavors(player.favors());

        List<String> slots = orderController.getAvailableSlots();
        view.showAvailableSlots(slots);
        int choice = view.promptSlotChoice(slots.size());
        String slotId = slots.get(choice - 1);

        SlotResultDTO res = orderController.chooseSlot(slotId);
        view.showSlotChoiceResult(slotId, res.ingredientCount(), res.favorCount());
    }
}
