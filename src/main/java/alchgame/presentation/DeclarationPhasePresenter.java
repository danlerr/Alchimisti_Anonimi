package alchgame.presentation;

import alchgame.controller.GameFlowController;
import alchgame.controller.RoundController;
import alchgame.model.player.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DeclarationPhasePresenter {

    private final GameFlowController gameFlow;
    private final RoundController roundController;
    private final GameView view;

    public DeclarationPhasePresenter(GameFlowController gameFlow, RoundController roundController, GameView view) {
        this.gameFlow = gameFlow;
        this.roundController = roundController;
        this.view = view;
    }

    public void run() {
        view.showPhaseHeader("DICHIARAZIONE");

        List<String> availableActions = roundController.getActionList();
        view.showBoard(roundController.getOrderAssignments(),
                availableActions,
                declarantsByAction(availableActions));

        List<Player> order = gameFlow.getDeclarationPhaseOrder();
        view.showPhaseOrder("DICHIARAZIONE", order.stream().map(Player::getName).toList());

        for (Player player : order) {
            gameFlow.setCurrentPlayer(player);
            view.showCurrentPlayer(player.getName());
            view.showPlayerStatus(player.getGold(),
                    player.getReputation(), player.getActionCubes());
            view.showIngredients(player.getIngredientsFromLab());

            while (roundController.canCurrentPlayerDeclare()) {
                view.showActionListWithPass(availableActions);
                int choice = view.promptActionOrPass(availableActions.size());
                if (choice == 0) break;
                String actionId = availableActions.get(choice - 1);
                try {
                    roundController.declareAction(actionId);
                    view.showDeclaredAction(player.getName(), actionId);
                    view.showBoard(roundController.getOrderAssignments(),
                            availableActions,
                            declarantsByAction(availableActions));
                    view.showPlayerStatus(player.getGold(),
                            player.getReputation(), player.getActionCubes());
                } catch (Exception e) {
                    view.showInvalidInput(e.getMessage());
                }
            }
        }
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
