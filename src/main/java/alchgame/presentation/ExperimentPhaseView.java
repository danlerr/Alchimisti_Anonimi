package alchgame.presentation;

import alchgame.controller.ExperimentController;
import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.Potion;
import alchgame.model.player.DeductionGrid;

import java.util.ArrayList;
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
        List<String> targetIds = new ArrayList<>(experimentController.getAvailableTargets().keySet());
        view.showTargetOptions(targetIds);
        String targetId = view.promptTargetChoice(targetIds);

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
        List<Ingredient> ingredients;
        try {
            ingredients = experimentController.getPlayerIngredients();
        } catch (IllegalStateException e) {
            view.showInvalidInput(e.getMessage());
            return;
        }
        view.showIngredients(ingredients);

        // 4. Scelta dei due ingredienti e tentativo esperimento (con retry su errore)
        Potion potion;
        while (true) {
            int firstChoice = view.promptIngredientChoice("  Primo ingrediente", ingredients.size());
            int secondChoice;
            do {
                secondChoice = view.promptIngredientChoice("  Secondo ingrediente", ingredients.size());
                if (secondChoice == firstChoice) {
                    view.showInvalidInput("Devi scegliere due ingredienti diversi.");
                }
            } while (secondChoice == firstChoice);

            try {
                String firstId  = ingredients.get(firstChoice - 1).getId();
                String secondId = ingredients.get(secondChoice - 1).getId();
                potion = experimentController.conductExperiment(targetId, firstId, secondId);
                break;
            } catch (Exception e) {
                view.showInvalidInput(e.getMessage() + " Riscegli gli ingredienti.");
            }
        }

        // 6. Risultato
        view.showPotionResult(potion);
        view.showIngredients(experimentController.getLabIngredients());

        // 7. Aggiornamento facoltativo della griglia di deduzione
        if (view.promptUpdateDeductionGrid()) {
            DeductionGrid grid = experimentController.getPlayerDeductionGrid();
            view.showDeductionGrid(grid);

            int ingChoice = view.promptDeductionIngredientChoice(grid.getIngredients().size());
            int alcChoice = view.promptDeductionAlchemicChoice(grid.getAlchemics().size());
            try {
                Ingredient ingredient   = grid.getIngredients().get(ingChoice - 1);
                AlchemicFormula formula = grid.getAlchemics().get(alcChoice - 1);
                experimentController.updateDeductionGrid(ingredient, formula);
                String ingName  = ingredient.getName();
                String alcLabel = "[" + alcChoice + "]";
                view.showExclusionResult(ingName, alcLabel);
            } catch (IllegalArgumentException e) {
                view.showInvalidInput("Esclusione non valida: " + e.getMessage());
            }
        }
    }
}
