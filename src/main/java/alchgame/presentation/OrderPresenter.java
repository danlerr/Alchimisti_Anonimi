package alchgame.presentation;

import alchgame.application.OrderController;
import alchgame.application.dto.BoardStateDTO;
import alchgame.application.dto.OrderSlotDTO;
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
        view.promptContinue("premi Invio per iniziare");
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

        List<OrderSlotDTO> availableSlots = board.orderSlots().stream()
                .filter(s -> !s.isTaken())
                .toList();
        view.showAvailableSlots(availableSlots);
        int choice = view.promptSlotChoice(availableSlots.size());
        String slotId = availableSlots.get(choice - 1).slotId();

        SlotResultDTO res = orderController.chooseSlot(slotId);
        view.showSlotChoiceResult(slotId, res.rewards(), res.receivedIngredients());

        List<String> favors = orderController.getPlayerFavors();
        while (!favors.isEmpty()) {
            view.showFavors(favors);
            int favorChoice = view.promptFavorOrSkip(favors.size());
            if (favorChoice == 0) break;
            orderController.activateFavor(favors.get(favorChoice - 1));
            favors = orderController.getPlayerFavors();
        }

        orderController.endTurn();
    }
}
