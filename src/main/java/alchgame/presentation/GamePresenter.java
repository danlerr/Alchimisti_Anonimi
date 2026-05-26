package alchgame.presentation;

import alchgame.controller.*;
import alchgame.model.board.Resources;
import alchgame.model.player.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GamePresenter {

    private final GameFlowController gameFlow;
    private final StartGameController startController;
    private final RoundController roundController;
    private final ActionDispatcher dispatcher;
    private final GameView view;

    public GamePresenter(
            GameFlowController gameFlow,
            StartGameController startController,
            RoundController roundController,
            ActionDispatcher dispatcher,
            GameView view
    ) {
        this.gameFlow = gameFlow;
        this.startController = startController;
        this.roundController = roundController;
        this.dispatcher = dispatcher;
        this.view = view;
    }

    public void run() {
        setup();

        while (true) {
            view.showRoundStart(gameFlow.getCurrentRoundNumber(), gameFlow.getTotalRounds());

            runOrderPhase();
            gameFlow.advancePhase();

            runDeclarationPhase();
            gameFlow.advancePhase();

            runResolutionPhase();

            view.showRoundEnd(gameFlow.getCurrentRoundNumber());

            if (gameFlow.getCurrentRoundNumber() >= gameFlow.getTotalRounds()) {
                break;
            }
            gameFlow.advanceRound();
        }

        view.showGameOver(gameFlow.getPlayers());
    }

    private void setup() {
        view.showWelcome();

        while (true) {
            view.promptPlayerCount(startController.getMinPlayers(), startController.getMaxPlayers());
            int n = view.readInt();
            try {
                startController.setPlayerNumber(n);
                break;
            } catch (IllegalArgumentException e) {
                view.showInvalidInput(e.getMessage());
            }
        }

        while (startController.needsMorePlayerNames()) {
            view.promptPlayerName(startController.getInsertedPlayersCount() + 1);
            while (true) {
                String name = view.readLine();
                try {
                    startController.setPlayerName(name);
                    break;
                } catch (IllegalArgumentException e) {
                    view.showInvalidInput(e.getMessage());
                    view.promptPlayerName(startController.getInsertedPlayersCount() + 1);
                }
            }
        }

        startController.startGame();
    }

    private void runOrderPhase() {
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
                    if (res.ingredientCount() > 0) {
                        view.showIngredients(player.getIngredientsFromLab());
                    }
                    if (res.favorCount() > 0) {
                        view.showFavors(player.getFavorCards());
                    }
                    break;
                } catch (Exception e) {
                    view.showInvalidInput(e.getMessage());
                }
            }
        }

        view.showWakeUpOrder(gameFlow.getWakeUpOrder().stream().map(Player::getName).toList());
    }

    private void runDeclarationPhase() {
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

            while (player.getActionCubes() > 0) {
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

    private void runResolutionPhase() {
        view.showPhaseHeader("RISOLUZIONE");

        int total = gameFlow.getResolutionTotalSteps();
        while (!gameFlow.isResolutionComplete()) {
            String actionId = gameFlow.currentResolutionActionId();
            Player player = gameFlow.currentResolutionPlayer();
            gameFlow.setCurrentPlayer(player);
            view.showResolutionStep(gameFlow.getResolutionStepIndex() + 1, total,
                    actionId, player.getName());
            dispatcher.dispatch(actionId);
            view.showPlayerStatus(player.getGold(),
                    player.getReputation(), player.getActionCubes());
            view.showIngredients(player.getIngredientsFromLab());
            gameFlow.markCurrentPlayerResolved();
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
