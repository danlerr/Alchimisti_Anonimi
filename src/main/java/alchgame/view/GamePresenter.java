package alchgame.view;

import alchgame.GameConfig;
import alchgame.controller.ExperimentHandler;
import alchgame.controller.StartGameHandler;
import alchgame.controller.TurnHandler;
import alchgame.model.*;
import alchgame.service.AlchGame;

import java.util.List;

public class GamePresenter {

    private final AlchGame          alchGame;
    private final ExperimentHandler experimentHandler;
    private final TurnHandler       turnHandler;
    private final StartGameHandler  startHandler;
    private final GameView          view;

    public GamePresenter(AlchGame alchGame,
                         ExperimentHandler experimentHandler,
                         TurnHandler turnHandler,
                         StartGameHandler startHandler,
                         GameView view) {
        this.alchGame          = alchGame;
        this.experimentHandler = experimentHandler;
        this.turnHandler       = turnHandler;
        this.startHandler      = startHandler;
        this.view              = view;
    }

    public void run() {
        view.clearScreen();
        view.printHeader();
        runSetupPhase();
        while (alchGame.isStarted()) {
            view.showRoundStart(alchGame.getCurrentRound(), alchGame.getTotalRounds());
            runOrderPhase();
            runDeclarationPhase();
            runResolutionPhase();
            view.showRoundEnd(alchGame.getCurrentRound());
            alchGame.endRound();
        }
        view.showGoodbye();
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    private void runSetupPhase() {
        view.printSection("NUOVA PARTITA");
        int count = view.askPlayerCount(AlchGame.MIN_PLAYERS, AlchGame.MAX_PLAYERS);
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
        for (Player player : alchGame.getOrderPhaseOrder()) {
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
        for (Player player : alchGame.getDeclarationPhaseOrder()) {
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
            List<Player> resolvers = alchGame.getResolutionOrderFor(actionId);
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

        boolean needsPayment = experimentHandler.paymentCheck(targetId);
        List<Ingredient> available;

        if (needsPayment) {
            if (!view.askPaymentConfirm(player.getGold())) {
                view.pause("  Esperimento annullato. Premi INVIO..."); return;
            }
            try {
                experimentHandler.payGold();
                available = experimentHandler.getIngredients();
                view.showPaymentSuccess(player.getGold());
            } catch (IllegalStateException e) {
                view.showError(e.getMessage()); return;
            }
        } else {
            available = experimentHandler.getIngredients();
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

        Potion potion = experimentHandler.conductExperiment(targetId, i1, i2);

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
        DeductionGrid grid = experimentHandler.getDeductionGrid();
        view.clearScreen();
        view.printSection("GRIGLIA DI DEDUZIONE");
        view.showDeductionGrid(GameViewModels.deductionGrid(grid));

        int ingChoice = view.askIngredientIndex(grid.getIngredients().size());
        if (ingChoice <= 0) return;
        Ingredient ingredient = grid.getIngredients().get(ingChoice - 1);

        int alcChoice = view.askAlchemicIndex(grid.getAlchemics().size());
        if (alcChoice <= 0) return;
        AlchemicFormula alchemic = grid.getAlchemics().get(alcChoice - 1);

        try {
            experimentHandler.updateDeductionGrid(ingredient, alchemic);
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
            return;
        }

        view.showDeductionSuccess(ingredient.getName(), alcChoice);
        view.pause("");
    }

}
