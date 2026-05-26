package alchgame.presentation;

import alchgame.controller.*;
import alchgame.model.board.Resources;
import alchgame.model.player.Player;

import java.util.List;

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
        showInitialLaboratories();
    }

    private void runOrderPhase() {
        view.showPhaseHeader("ORDINE");

        for (Player player : gameFlow.getOrderPhaseOrder()) {
            gameFlow.setCurrentPlayer(player);
            view.showCurrentPlayer(player.getName());
            view.showPlayerStatus(player.getName(), player.getGold(),
                    player.getReputation(), player.getActionCubes());
            view.showIngredients(player.getIngredientsFromLab());

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
                    break;
                } catch (Exception e) {
                    view.showInvalidInput(e.getMessage());
                }
            }
        }
    }

    private void runDeclarationPhase() {
        view.showPhaseHeader("DICHIARAZIONE");

        List<String> availableActions = roundController.getActionList();

        for (Player player : gameFlow.getDeclarationPhaseOrder()) {
            gameFlow.setCurrentPlayer(player);
            view.showCurrentPlayer(player.getName());
            view.showPlayerStatus(player.getName(), player.getGold(),
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
                    view.showPlayerStatus(player.getName(), player.getGold(),
                            player.getReputation(), player.getActionCubes());
                } catch (Exception e) {
                    view.showInvalidInput(e.getMessage());
                }
            }
        }
    }

    private void runResolutionPhase() {
        view.showPhaseHeader("RISOLUZIONE");

        while (!gameFlow.isResolutionComplete()) {
            String actionId = gameFlow.currentResolutionActionId();
            Player player = gameFlow.currentResolutionPlayer();
            gameFlow.setCurrentPlayer(player);
            view.showCurrentPlayer(player.getName());
            dispatcher.dispatch(actionId);
            gameFlow.markCurrentPlayerResolved();
        }
    }

    private void showInitialLaboratories() {
        view.showPhaseHeader("LABORATORI INIZIALI");
        for (Player player : gameFlow.getPlayers()) {
            view.showCurrentPlayer(player.getName());
            view.showIngredients(player.getIngredientsFromLab());
        }
    }
}
