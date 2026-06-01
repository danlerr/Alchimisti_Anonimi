package alchgame.presentation;

import alchgame.application.DeclarationController;
import alchgame.model.player.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DeclarationPhasePresenter {

    private final DeclarationController declarationController;
    private final GameView view;

    public DeclarationPhasePresenter(DeclarationController declarationController, GameView view) {
        this.declarationController = declarationController;
        this.view = view;
    }

    public void run() {
        view.showPhaseHeader("DICHIARAZIONE");

        List<String> availableActions = declarationController.getActionList();
        view.showBoard(declarationController.getOrderAssignments(),
                availableActions,
                declarantsByAction(availableActions));

        List<Player> order = declarationController.getTurnOrder();
        view.showPhaseOrder("DICHIARAZIONE", order.stream().map(Player::getName).toList());

        while (!declarationController.isPhaseComplete()) {
            Player player = declarationController.getCurrentPlayer();
            view.showCurrentPlayer(player.getName());
            view.showPlayerStatus(player.getGold(), player.getReputation(), player.getActionCubes());
            view.showIngredients(player.getIngredientsFromLab());

            while (declarationController.canCurrentPlayerDeclare()) {
                view.showActionListWithPass(availableActions);
                int choice = view.promptActionOrPass(availableActions.size());
                if (choice == 0) break;
                String actionId = availableActions.get(choice - 1);
                try {
                    declarationController.declareAction(actionId);
                    view.showDeclaredAction(player.getName(), actionId);
                    view.showBoard(declarationController.getOrderAssignments(),
                            availableActions,
                            declarantsByAction(availableActions));
                    view.showPlayerStatus(player.getGold(), player.getReputation(), player.getActionCubes());
                } catch (Exception e) {
                    view.showInvalidInput(e.getMessage());
                }
            }

            declarationController.advanceTurn();
        }
    }

    private Map<String, List<String>> declarantsByAction(List<String> actionIds) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String id : actionIds) {
            result.put(id, declarationController.getDeclaredPlayers(id).stream()
                    .map(Player::getName).toList());
        }
        return result;
    }
}
