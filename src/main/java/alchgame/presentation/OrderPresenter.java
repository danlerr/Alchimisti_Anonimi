package alchgame.presentation;

import alchgame.application.GameController;
import alchgame.application.OrderController;
import alchgame.application.dto.BoardStateDTO;
import alchgame.application.dto.PlayerDTO;
import alchgame.application.dto.SlotResultDTO;
import alchgame.application.observer.GameStateDTO;

import java.util.List;

public class OrderPresenter {

    private final OrderController orderController;
    private final GameController gameController;
    private final GameView view;

    public OrderPresenter(OrderController orderController,
                               GameController gameController,
                               GameView view) {
        this.orderController = orderController;
        this.gameController = gameController;
        this.view = view;
    }

    public void showPhaseStart() {
        view.showPhaseHeader("Scelta dell'ordine nel tracciato di risveglio");
    }

    public void showPhaseEnd(GameStateDTO state) {
        view.showWakeUpOrder(state.boardState().wakeUpOrder());
    }

    public void handleTurn(GameStateDTO state) {
        PlayerDTO player = state.currentPlayer();
        BoardStateDTO board = state.boardState();

        view.showBoard(board.orderSlots(), board.actionIds(), board.declarantsByAction());
        view.showCurrentPlayer(player.name());
        view.showPlayerStatus(player.gold(), player.reputation(), player.actionCubes());
        view.showIngredients(player.ingredients().stream()
                .map(i -> i.name()).toList());
        view.showFavors(player.favors());

        if (!state.turnContinues()) {
            gameController.endTurn();
            return;
        }

        List<String> slots = orderController.getAvailableSlots();
        view.showAvailableSlots(slots);
        int choice = view.promptSlotChoice(slots.size());
        String slotId = slots.get(choice - 1);

        SlotResultDTO res = orderController.chooseSlot(slotId);
        view.showSlotChoiceResult(slotId, res.ingredientCount(), res.favorCount());
    }
}
