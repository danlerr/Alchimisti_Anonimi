package alchgame.presentation;

import alchgame.application.DeclarationController;
import alchgame.model.board.Board;
import alchgame.model.game.Round;
import alchgame.model.game.phase.DeclarationPhase;
import alchgame.model.player.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DeclarationPhasePresenter {

    private final DeclarationController declarationController;
    private final Board board;
    private final Supplier<Round> roundSupplier;
    private final GameView view;

    public DeclarationPhasePresenter(DeclarationController declarationController,
                                     Board board,
                                     Supplier<Round> roundSupplier,
                                     GameView view) {
        this.declarationController = declarationController;
        this.board = board;
        this.roundSupplier = roundSupplier;
        this.view = view;
    }

    public void run() {
        Round round = roundSupplier.get();
        List<String> availableActions = declarationController.getActionList();

        view.showPhaseHeader("DICHIARAZIONE");
        view.showBoard(orderSlots(), availableActions, declarantsByAction(availableActions));

        while (round.getCurrentPhase() instanceof DeclarationPhase) {
            Player player = declarationController.getCurrentPlayer();

            view.showCurrentPlayer(player.getName());
            view.showPlayerStatus(player.getGold(), player.getReputation(), player.getActionCubes());
            view.showIngredients(player.getIngredientsFromLab().stream()
                    .map(i -> i.getName()).toList());

            // Loop di dichiarazione: il giocatore dichiara azioni o passa
            while (true) {
                view.showBoard(orderSlots(), availableActions, declarantsByAction(availableActions));
                view.showActionListWithPass(availableActions);
                int choice = view.promptActionOrPass(availableActions.size());

                if (choice == 0) {
                    // Passa: avanza il turno tramite la catena observer
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
