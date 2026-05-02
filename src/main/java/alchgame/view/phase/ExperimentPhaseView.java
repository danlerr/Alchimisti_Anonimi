package alchgame.view.phase;

import alchgame.GameConfig;
import alchgame.controller.ExperimentController;
import alchgame.model.alchemy.Potion;
import alchgame.model.player.DeductionGrid;
import alchgame.view.GameView;
import alchgame.view.viewmodel.DeductionGridView;
import alchgame.view.viewmodel.GameViewModels;

import java.util.List;

public class ExperimentPhaseView {

    private final GameView             view;
    private final ExperimentController experimentController;

    public ExperimentPhaseView(GameView view, ExperimentController experimentController) {
        this.view                 = view;
        this.experimentController = experimentController;
    }

    public void run() {
        view.clearScreen();
        view.printSection("INIZIA ESPERIMENTO");

        Integer targetChoice = view.askTargetChoice();
        if (targetChoice == null) return;
        String targetId = targetChoice == 1 ? GameConfig.TARGET_STUDENT_ID : GameConfig.TARGET_SELF_ID;

        boolean needsPayment = experimentController.paymentCheck(targetId);
        List<String> ingredientNames;

        if (needsPayment) {
            if (!view.askPaymentConfirm(experimentController.getCurrentPlayerGold())) {
                view.pause("  Esperimento annullato. Premi INVIO...");
                return;
            }
            try {
                int remainingGold = experimentController.payGold();
                ingredientNames   = experimentController.getIngredientNames();
                view.showPaymentSuccess(remainingGold);
            } catch (IllegalStateException e) {
                view.showError(e.getMessage());
                return;
            }
        } else {
            ingredientNames = experimentController.getIngredientNames();
        }

        if (ingredientNames.size() < 2) {
            view.showError("Non hai abbastanza ingredienti!");
            return;
        }

        view.showIngredients(ingredientNames);

        Integer firstIdx = view.pickIngredient(ingredientNames, "  Scegli il 1° ingrediente > ", null);
        if (firstIdx == null) return;

        Integer secondIdx = view.pickIngredient(ingredientNames, "  Scegli il 2° ingrediente > ", firstIdx);
        if (secondIdx == null) return;

        Potion potion = experimentController.conductExperiment(targetId, firstIdx, secondIdx);

        view.clearScreen();
        view.printSection("RISULTATO ESPERIMENTO");
        view.showExperimentResult(
                GameViewModels.experimentResult(ingredientNames.get(firstIdx), ingredientNames.get(secondIdx), potion));

        if (GameConfig.TARGET_STUDENT_ID.equals(targetId)) {
            view.showStudentEffect(experimentController.getStudentStateName(GameConfig.TARGET_STUDENT_ID));
        } else {
            view.showPlayerEffect(experimentController.getCurrentPlayerReputation());
        }

        view.pause("\n  Premi INVIO per continuare...");

        if (view.askDeductionConfirm()) runDeductionFlow();
    }

    private void runDeductionFlow() {
        DeductionGrid grid         = experimentController.getDeductionGrid();
        DeductionGridView gridView = GameViewModels.deductionGrid(grid);
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
