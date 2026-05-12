package alchgame.presentation;

import alchgame.controller.*;
import alchgame.model.board.Resources;
import alchgame.model.player.Player;
import alchgame.resources.GameConfig;

import java.util.List;

public class GamePresenter {

    private final GameFlowController gameFlow;
    private final StartGameController startController;
    private final RoundController roundController;
    private final ExperimentPhaseView experimentPhaseView;
    private final ActionResolverRegistry actionRegistry;
    private final GameView view;

    public GamePresenter(
            GameFlowController gameFlow,
            StartGameController startController,
            RoundController roundController,
            ExperimentPhaseView experimentPhaseView,
            ActionResolverRegistry actionRegistry,
            GameView view
    ) {
        this.gameFlow = gameFlow;
        this.startController = startController;
        this.roundController = roundController;
        this.experimentPhaseView = experimentPhaseView;
        this.actionRegistry = actionRegistry;
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

        // Raccolta numero giocatori
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

        // Raccolta nomi giocatori
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
        view.showPhaseHeader("ORDINE");

        for (Player player : gameFlow.getOrderPhaseOrder()) {
            gameFlow.setCurrentPlayer(player);
            view.showCurrentPlayer(player.getName());
            view.showPlayerStatus(player.getName(), player.getGold(),
                    player.getReputation(), player.getActionCubes());

            while (true) {
                List<String> slots = roundController.getAvailableSlots();
                view.showAvailableSlots(slots);
                int choice = view.promptSlotChoice(slots.size());
                String slotId = slots.get(choice - 1);
                try {
                    Resources res = roundController.chooseSlot(slotId);
                    view.showSlotChoiceResult(slotId, res);
                    break;
                } catch (Exception e) {
                    view.showInvalidInput(e.getMessage());
                }
            }
        }
    }

    private void runDeclarationPhase() {
        view.showPhaseHeader("DICHIARAZIONE");

        // Solo le azioni che hanno un controller registrato
        List<String> availableActions = roundController.getActionList().stream()
                .filter(actionRegistry::contains)
                .toList();

        for (Player player : gameFlow.getDeclarationPhaseOrder()) {
            gameFlow.setCurrentPlayer(player);
            view.showCurrentPlayer(player.getName());
            view.showPlayerStatus(player.getName(), player.getGold(),
                    player.getReputation(), player.getActionCubes());

            while (true) {
                view.showActionList(availableActions);
                int choice = view.promptActionChoice(availableActions.size());
                String actionId = availableActions.get(choice - 1);
                try {
                    roundController.declareAction(actionId);
                    view.showDeclaredAction(player.getName(), actionId);
                    break;
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
            dispatchAction(actionId);
            gameFlow.markCurrentPlayerResolved();
        }
    }

    private void dispatchAction(String actionId) {
        switch (actionId) {
            case GameConfig.AS_EXPERIMENT ->
                experimentPhaseView.run();
            case GameConfig.AS_FORAGE ->
                ((ForageController) actionRegistry.get(actionId)).execute();
            case GameConfig.AS_TRANSMUTE ->
                ((TransmuteController) actionRegistry.get(actionId)).execute();
            case GameConfig.AS_SELL_POTION ->
                ((SellPotionController) actionRegistry.get(actionId)).execute();
            default ->
                view.showInvalidInput("Azione sconosciuta: " + actionId);
        }
    }
}
