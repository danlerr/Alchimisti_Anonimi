package alchgame.view;

import alchgame.GameConfig;
import alchgame.controller.ExperimentController;
import alchgame.controller.StartGameController;
import alchgame.controller.TurnController;
import alchgame.model.alchemy.*;
import alchgame.model.board.*;
import alchgame.model.player.*;
import alchgame.model.game.*;
import alchgame.service.GameFlowService;

import java.util.List;

public class GamePresenter {

    private final GameSession          alchGame;
    private final GameFlowService   gameFlowService;
    private final ExperimentController experimentController;
    private final TurnController       turnHandler;
    private final StartGameController  startHandler;
    private final GameView          view;

    public GamePresenter(GameSession alchGame,
                         GameFlowService gameFlowService,
                         ExperimentController experimentHandler,
                         TurnController turnHandler,
                         StartGameController startHandler,
                         GameView view) {
        this.alchGame          = alchGame;
        this.gameFlowService   = gameFlowService;
        this.experimentController = experimentHandler;
        this.turnHandler       = turnHandler;
        this.startHandler      = startHandler;
        this.view              = view;
    }

    public void run() {
        view.clearScreen();
        view.printHeader();
        runSetupPhase();
        while (alchGame.isStarted()) {
            view.showRoundStart(gameFlowService.getCurrentRound(), gameFlowService.getTotalRounds());
            runOrderPhase();
            runDeclarationPhase();
            runResolutionPhase();
            view.showRoundEnd(gameFlowService.getCurrentRound());
            gameFlowService.endRound();
        }
        view.showGoodbye();
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    private void runSetupPhase() {
        view.printSection("NUOVA PARTITA");
        int count = view.askPlayerCount(startHandler.getMinPlayers(), startHandler.getMaxPlayers());
        startHandler.setPlayerNumber(count);
        for (int i = 1; i <= count; i++) {
            try {
                startHandler.setPlayerName(view.askPlayerName(i));
            } catch (IllegalArgumentException e) {
                view.showError(e.getMessage());
                i--;
            }
        }
        startHandler.startGame();
    }

    // ── Fase Ordine ───────────────────────────────────────────────────────────

    private void runOrderPhase() {
        view.printSection("FASE ORDINE DI RISVEGLIO");
        List<GameConfig.SlotSpec> allSlots = GameConfig.SLOTS;
        for (Player player : gameFlowService.getOrderPhaseOrder()) {
            alchGame.setCurrentPlayer(player);
            view.showOrderTurn(player.getName());
            List<String> freeSlots = alchGame.getBoard().getAvailableSlotIds();
            String slotId = view.askSlotChoice(GameViewModels.orderSlots(freeSlots, allSlots));
            Resources res = turnHandler.chooseSlot(slotId);
            view.showSlotAssigned(GameViewModels.slotAssignment(player.getName(), slotId, res));
        }
        view.showWakeUpOrder(GameViewModels.playerNames(alchGame.getBoard().getWakeUpOrder()));
    }

    // ── Fase Dichiarazione ────────────────────────────────────────────────────

    private void runDeclarationPhase() {
        view.printSection("FASE DICHIARAZIONE AZIONI");
        for (Player player : gameFlowService.getDeclarationPhaseOrder()) {
            alchGame.setCurrentPlayer(player);
            while (player.getActionCubes() > 0) {
                String actionId = view.askActionDeclaration(
                    player.getName(), GameConfig.ACTION_ORDER, player.getActionCubes());
                if (actionId == null) break;
                try {
                    turnHandler.declareAction(actionId);
                } catch (IllegalStateException e) {
                    view.showError(e.getMessage());
                }
            }
        }
    }

    // ── Fase Risoluzione ──────────────────────────────────────────────────────

    private void runResolutionPhase() {
        view.printSection("FASE RISOLUZIONE");
        for (String actionId : GameConfig.ACTION_ORDER) {
            List<Player> resolvers = gameFlowService.getResolutionOrderFor(actionId);
            if (resolvers.isEmpty()) continue;
            view.showResolutionStart(actionId);
            for (Player player : resolvers) {
                alchGame.setCurrentPlayer(player);
                view.showResolvingPlayer(player.getName(), actionId);
                resolveActionFor(player, actionId);
            }
        }
    }

    private void resolveActionFor(Player player, String actionId) {
        switch (actionId) {
            case GameConfig.AS_EXPERIMENT -> runExperimentFlow(player);
            default -> view.pause("  [" + actionId + "] non ancora implementato. Premi INVIO...");
        }
    }

    // ── Esperimento ───────────────────────────────────────────────────────────

    private void runExperimentFlow(Player player) {
        view.clearScreen();
        view.printSection("INIZIA ESPERIMENTO");

        Integer targetChoice = view.askTargetChoice();
        if (targetChoice == null) return;
        String targetId = targetChoice == 1 ? GameConfig.TARGET_STUDENT_ID : GameConfig.TARGET_SELF_ID;

        boolean needsPayment = experimentController.paymentCheck(targetId);
        List<Ingredient> available;

        if (needsPayment) {
            if (!view.askPaymentConfirm(player.getGold())) {
                view.pause("  Esperimento annullato. Premi INVIO..."); return;
            }
            try {
                experimentController.payGold();
                available = experimentController.getIngredients();
                view.showPaymentSuccess(player.getGold());
            } catch (IllegalStateException e) {
                view.showError(e.getMessage()); return;
            }
        } else {
            available = experimentController.getIngredients();
        }

        if (available.size() < 2) {
            view.showError("Non hai abbastanza ingredienti!"); return;
        }

        List<String> ingredientNames = GameViewModels.ingredientNames(available);
        view.showIngredients(ingredientNames);

        Integer firstIndex = view.pickIngredient(ingredientNames, "  Scegli il 1° ingrediente > ", null);
        if (firstIndex == null) return;
        Ingredient i1 = available.get(firstIndex);

        Integer secondIndex = view.pickIngredient(ingredientNames, "  Scegli il 2° ingrediente > ", firstIndex);
        if (secondIndex == null) return;
        Ingredient i2 = available.get(secondIndex);

        Potion potion = experimentController.conductExperiment(targetId, i1, i2);

        view.clearScreen();
        view.printSection("RISULTATO ESPERIMENTO");
        view.showExperimentResult(GameViewModels.experimentResult(i1, i2, potion));

        if (GameConfig.TARGET_STUDENT_ID.equals(targetId)) {
            Student student = (Student) alchGame.getTarget(GameConfig.TARGET_STUDENT_ID);
            view.showStudentEffect(student.getState().name());
        } else {
            view.showPlayerEffect(player.getReputation());
        }

        view.pause("\n  Premi INVIO per continuare...");

        if (view.askDeductionConfirm()) runDeductionFlow();
    }

    private void runDeductionFlow() {
        DeductionGridView gridView = GameViewModels.deductionGrid(experimentController.getDeductionGrid());
        view.clearScreen();
        view.printSection("GRIGLIA DI DEDUZIONE");
        view.showDeductionGrid(gridView);

        int ingChoice = view.askIngredientIndex(gridView.ingredientNames().size());
        if (ingChoice <= 0) return;

        int alcChoice = view.askAlchemicIndex(gridView.alchemicLabels().size());
        if (alcChoice <= 0) return;

        try {
            experimentController.updateDeductionGrid(ingChoice - 1, alcChoice - 1);
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
            return;
        }

        view.showDeductionSuccess(gridView.ingredientNames().get(ingChoice - 1), alcChoice);
        view.pause("");
    }

}
