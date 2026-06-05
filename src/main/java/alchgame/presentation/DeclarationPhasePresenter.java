package alchgame.presentation;

import alchgame.application.DeclarationController;
import alchgame.application.dto.BoardStateDTO;
import alchgame.application.dto.PlayerDTO;
import alchgame.application.observer.GameStateDTO;

import java.util.List;

public class DeclarationPhasePresenter {

    private final DeclarationController declarationController;
    private final GameView view;

    public DeclarationPhasePresenter(DeclarationController declarationController, GameView view) {
        this.declarationController = declarationController;
        this.view = view;
    }

    public void showPhaseStart() {
        view.showPhaseHeader("DICHIARAZIONE");
    }

    public void handleTurn(GameStateDTO state) {
        PlayerDTO player = state.currentPlayer();
        BoardStateDTO board = state.boardState();
        List<String> availableActions = declarationController.getActionList();

        view.showCurrentPlayer(player.name());
        view.showPlayerStatus(player.gold(), player.reputation(), player.actionCubes());
        view.showIngredients(player.ingredients().stream()
                .map(i -> i.name()).toList());

        while (true) {
            view.showBoard(board.orderSlots(), availableActions,
                    board.declarantsByAction());
            view.showActionListWithPass(availableActions);
            int choice = view.promptActionOrPass(availableActions.size());

            if (choice == 0) {
                declarationController.endDeclaration();
                break;
            }

            String actionId = availableActions.get(choice - 1);
            try {
                declarationController.declareAction(actionId);
                view.showDeclaredAction(player.name(), actionId);
                declarationController.endDeclaration();
                break;
            } catch (Exception e) {
                view.showInvalidInput(e.getMessage());
            }
        }
    }
}
