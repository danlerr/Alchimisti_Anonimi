package alchgame.view.phase;

import alchgame.GameConfig;
import alchgame.controller.ExperimentController;
import alchgame.dto.ExperimentResultState;
import alchgame.dto.ExperimentSetupState;
import alchgame.model.player.DeductionGrid;

import java.util.List;
import alchgame.view.GameView;
import alchgame.view.viewmodel.DeductionGridView;
import alchgame.view.viewmodel.GameViewModels;

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
        String targetId = targetChoice == 1 ? GameConfig.TARGET_STUDENT_ID : GameConfig.SELF_ID;

        boolean needsPayment = experimentController.paymentCheck(targetId);
        ExperimentSetupState setup = experimentController.getExperimentSetupState();

        if (needsPayment) {
            if (!view.askPaymentConfirm(setup.currentGold())) {
                view.pause("  Esperimento annullato. Premi INVIO...");
                return;
            }
            try {
                int remainingGold = experimentController.payGold();
                view.showPaymentSuccess(remainingGold);
            } catch (IllegalStateException e) {
                view.showError(e.getMessage());
                return;
            }
        }

        List<String> ingredientNames = setup.ingredientNames();

        if (ingredientNames.size() < 2) {
            view.showError("Non hai abbastanza ingredienti!");
            return;
        }

        view.showIngredients(ingredientNames);

        Integer firstIdx = view.pickIngredient(ingredientNames, "  Scegli il 1° ingrediente > ", null);
        if (firstIdx == null) return;

        Integer secondIdx = view.pickIngredient(ingredientNames, "  Scegli il 2° ingrediente > ", firstIdx);
        if (secondIdx == null) return;

        ExperimentResultState result = experimentController.conductExperiment(targetId, firstIdx, secondIdx);

        view.clearScreen();
        view.printSection("RISULTATO ESPERIMENTO");
        view.showExperimentResult(
                GameViewModels.experimentResult(ingredientNames.get(firstIdx), ingredientNames.get(secondIdx), result.potion()));

        if (result.isStudentTarget()) {
            view.showStudentEffect(result.studentStateName());
        } else {
            view.showPlayerEffect(result.playerReputation());
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
