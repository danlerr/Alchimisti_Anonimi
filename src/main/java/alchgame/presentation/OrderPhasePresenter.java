package alchgame.presentation;

import alchgame.controller.GameFlowController;
import alchgame.controller.RoundController;
import alchgame.model.board.Resources;
import alchgame.model.player.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderPhasePresenter {

    private final GameFlowController gameFlow;
    private final RoundController roundController;
    private final GameView view;

    public OrderPhasePresenter(GameFlowController gameFlow, RoundController roundController, GameView view) {
        this.gameFlow = gameFlow;
        this.roundController = roundController;
        this.view = view;
    }

    public void run() {
        view.showPhaseHeader("Scelta dell'ordine nel tracciato di risveglio");
        view.showBoard(roundController.getOrderAssignments(),
                roundController.getActionList(),
                declarantsByAction(roundController.getActionList()));

        List<Player> order = gameFlow.getOrderPhaseOrder();
        view.showPhaseOrder("di scelta della posizione nel tracciato di risveglio", order.stream().map(Player::getName).toList());

        for (Player player : order) {
            gameFlow.setCurrentPlayer(player);
            view.showCurrentPlayer(player.getName());
            view.showPlayerStatus(player.getGold(),
                    player.getReputation(), player.getActionCubes());
            view.showIngredients(player.getIngredientsFromLab());
            view.showOrderAssignments(roundController.getOrderAssignments());

            while (true) {
                List<String> slots = roundController.getAvailableSlots();
                view.showAvailableSlots(slots);
                int choice = view.promptSlotChoice(slots.size());
                String slotId = slots.get(choice - 1);
                try {
                    Resources res = roundController.chooseSlot(slotId);
                    view.showSlotChoiceResult(slotId, res);
                    view.showIngredients(player.getIngredientsFromLab());
                    view.showFavors(player.getFavorCards());
                    break;
                } catch (Exception e) {
                    view.showInvalidInput(e.getMessage());
                }
            }
        }

        view.showWakeUpOrder(gameFlow.getWakeUpOrder().stream().map(Player::getName).toList());
    }

    private Map<String, List<String>> declarantsByAction(List<String> actionIds) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String id : actionIds) {
            result.put(id, roundController.getDeclaredPlayers(id).stream()
                    .map(Player::getName).toList());
        }
        return result;
    }
}
