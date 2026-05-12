package alchgame.presentation;

import alchgame.controller.ExperimentController;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.Potion;
import alchgame.model.player.DeductionGrid;
import alchgame.resources.GameConfig;

import java.util.List;

public class ExperimentPhaseView {

    private final GameView view;
    private final ExperimentController experimentController;

    public ExperimentPhaseView(GameView view, ExperimentController experimentController) {
        this.view = view;
        this.experimentController = experimentController;
    }

    public void run() {
        // 1. Scelta bersaglio
        view.showTargetOptions(GameConfig.SELF_ID, GameConfig.TARGET_STUDENT_ID);
        String targetId = view.promptTargetChoice(GameConfig.SELF_ID, GameConfig.TARGET_STUDENT_ID);

        // 2. Pagamento se lo studente è scontento
        if (experimentController.paymentCheck(targetId)) {
            view.showPaymentRequired();
            try {
                int remaining = experimentController.payGold();
                view.showPaymentResult(remaining);
            } catch (IllegalStateException e) {
                view.showInsufficientGold();
                return;
            }
        }

        // 3. Lista ingredienti dal laboratorio
        List<Ingredient> ingredients = experimentController.getLabIngredients();
        if (ingredients.size() < 2) {
            view.showInvalidInput("Non hai abbastanza ingredienti per condurre un esperimento.");
            return;
        }
        view.showIngredients(ingredients);

        // 4. Scelta dei due ingredienti (1-based in UI, 0-based internamente)
        int firstChoice = view.promptIngredientChoice("  Primo ingrediente", ingredients.size());
        int secondChoice;
        do {
            secondChoice = view.promptIngredientChoice("  Secondo ingrediente", ingredients.size());
            if (secondChoice == firstChoice) {
                view.showInvalidInput("Devi scegliere due ingredienti diversi.");
            }
        } while (secondChoice == firstChoice);

        int firstIdx  = firstChoice  - 1;
        int secondIdx = secondChoice - 1;

        // 5. Conduci esperimento
        Potion potion;
        try {
            potion = experimentController.conductExperiment(targetId, firstIdx, secondIdx);
        } catch (Exception e) {
            view.showInvalidInput("Errore durante l'esperimento: " + e.getMessage());
            return;
        }

        // 6. Risultato
        view.showPotionResult(potion);

        // 7. Aggiornamento facoltativo della griglia di deduzione
        if (view.promptUpdateDeductionGrid()) {
            DeductionGrid grid = experimentController.getDeductionGrid();
            view.showDeductionGrid(grid);

            int ingChoice = view.promptDeductionIngredientChoice(grid.getIngredients().size());
            int alcChoice = view.promptDeductionAlchemicChoice(grid.getAlchemics().size());
            int ingIdx = ingChoice - 1;
            int alcIdx = alcChoice - 1;

            try {
                experimentController.updateDeductionGrid(ingIdx, alcIdx);
                String ingName = grid.getIngredients().get(ingIdx).getName();
                String alcLabel = "[" + alcChoice + "]";
                view.showExclusionResult(ingName, alcLabel);
            } catch (IllegalArgumentException e) {
                view.showInvalidInput("Esclusione non valida: " + e.getMessage());
            }
        }
    }
}
