package alchgame.presentation;

import alchgame.application.DeclarationController;
import alchgame.application.observer.GameStateDTO;
import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DeclarationPhasePresenter {

    private final DeclarationController declarationController;
    private final Board board;
    private final GameView view;

    public DeclarationPhasePresenter(DeclarationController declarationController, Board board, GameView view) {
        this.declarationController = declarationController;
        this.board = board;
        this.view = view;
    }

    public void showPhaseStart() {
        view.showPhaseHeader("DICHIARAZIONE");
    }

    public void handleTurn(GameStateDTO state) {
        Player player = state.currentPlayer();
        List<String> availableActions = declarationController.getActionList();

        view.showCurrentPlayer(player.getName());
        view.showPlayerStatus(player.getGold(), player.getReputation(), player.getActionCubes());
        view.showIngredients(player.getIngredientsFromLab().stream()
                .map(i -> i.getName()).toList());

        while (true) {
            view.showBoard(orderSlots(), availableActions, declarantsByAction(availableActions));
            view.showActionListWithPass(availableActions);
            int choice = view.promptActionOrPass(availableActions.size());

            if (choice == 0) {
                declarationController.endDeclaration();
                break;
            }

            String actionId = availableActions.get(choice - 1);
            try {
                declarationController.declareAction(actionId);
                view.showDeclaredAction(player.getName(), actionId);
                view.showPlayerStatus(player.getGold(), player.getReputation(), player.getActionCubes());
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

    private Map<String, List<String>> declarantsByAction(List<String> actionIds) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String id : actionIds)
            result.put(id, board.getActionSpace(id).getDeclaredPlayers().stream()
                    .map(Player::getName).toList());
        return result;
    }
}
