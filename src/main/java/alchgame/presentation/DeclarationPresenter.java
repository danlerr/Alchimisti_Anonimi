package alchgame.presentation;

import alchgame.application.DeclarationController;
import alchgame.application.dto.BoardStateDTO;
import alchgame.application.dto.PlayerDTO;
import alchgame.application.dto.GameStateDTO;

import java.util.List;

public class DeclarationPresenter {

    private final DeclarationController declarationController;
    private final GameView view;

    public DeclarationPresenter(DeclarationController declarationController, GameView view) {
        this.declarationController = declarationController;
        this.view = view;
    }

    public void showPhaseStart() {
        view.showPhaseHeader("DICHIARAZIONE");
    }

    public void handleTurn(GameStateDTO state) {
        PlayerDTO player = state.currentPlayer();
        BoardStateDTO board = state.boardState();
        List<String> actions = declarationController.getActionList();

        view.showCurrentPlayer(player.name());
        view.showPlayerStatus(player.gold(), player.reputation(), player.actionCubes());
        view.showIngredients(player.ingredients().stream().map(i -> i.name()).toList());
        view.showBoard(board.orderSlots(), actions, board.declarantsByAction());
        view.showActionListWithPass(actions);

        int choice = view.promptActionOrPass(actions.size());
        if (choice == 0) {
            declarationController.pass();                          // azione di gioco
        } else {
            declarationController.declareAction(actions.get(choice - 1));  // azione di gioco
        }
        // Il controller decide il resto via onActionPerformed()
    }
}
