package alchgame.presentation;

import alchgame.application.DeclarationController;
import alchgame.application.GameController;
import alchgame.application.dto.BoardStateDTO;
import alchgame.application.dto.PlayerDTO;
import alchgame.application.observer.GameStateDTO;

import java.util.List;

public class DeclarationPresenter {

    private final DeclarationController declarationController;
    private final GameController gameController;
    private final GameView view;

    public DeclarationPresenter(DeclarationController declarationController,
                                     GameController gameController,
                                     GameView view) {
        this.declarationController = declarationController;
        this.gameController = gameController;
        this.view = view;
    }

    public void showPhaseStart() {
        view.showPhaseHeader("DICHIARAZIONE");
    }

    /**
     * Reattivo e senza loop: renderizza lo stato fresco dell'evento, poi compie
     * UNA scelta. La ripetizione (piazzare più cubi) è guidata dagli eventi
     * TURN_REFRESHED prodotti da {@code declareAction}; l'avanzamento di turno
     * passa sempre da {@code gameController.endTurn()}.
     */
    public void handleTurn(GameStateDTO state) {
        PlayerDTO player = state.currentPlayer();
        BoardStateDTO board = state.boardState();
        List<String> availableActions = declarationController.getActionList();

        view.showCurrentPlayer(player.name());
        view.showPlayerStatus(player.gold(), player.reputation(), player.actionCubes());
        view.showIngredients(player.ingredients().stream()
                .map(i -> i.name()).toList());

        // Nessun cubo residuo (turno fresco senza cubi o dopo l'ultimo cubo): fine turno.
        if (!state.turnContinues()) {
            gameController.endTurn();
            return;
        }

        view.showBoard(board.orderSlots(), availableActions, board.declarantsByAction());
        view.showActionListWithPass(availableActions);
        int choice = view.promptActionOrPass(availableActions.size());

        if (choice == 0) {
            gameController.endTurn();
            return;
        }

        String actionId = availableActions.get(choice - 1);
        declarationController.declareAction(actionId);   // → onActionPerformed → TURN_REFRESHED
        view.showDeclaredAction(player.name(), actionId);
    }
}
