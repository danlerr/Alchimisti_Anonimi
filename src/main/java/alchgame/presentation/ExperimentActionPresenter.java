package alchgame.presentation;

import alchgame.application.ExperimentController;
import alchgame.application.dto.DeductionGridDTO;
import alchgame.application.dto.IngredientDTO;
import alchgame.application.dto.PotionDTO;

import java.util.ArrayList;
import java.util.List;

public class ExperimentActionPresenter {

    private final GameView view;
    private final ExperimentController experimentController;

    public ExperimentActionPresenter(GameView view, ExperimentController experimentController) {
        this.view = view;
        this.experimentController = experimentController;
    }

    public void run() {
        // 1. Scelta bersaglio
        List<String> targetIds = new ArrayList<>(experimentController.getTargetIds());
        view.showTargetOptions(targetIds);
        String targetId = view.promptTargetChoice(targetIds);

        // 2. Pagamento se richiesto
        if (experimentController.paymentCheck(targetId)) {
            view.showPaymentRequired();
            try {
                int remaining = experimentController.payGold(targetId);
                view.showPaymentResult(remaining);
            } catch (IllegalStateException e) {
                view.showInsufficientGold();
                return;
            }
        }

        // 3. Lista ingredienti
        List<IngredientDTO> ingredients = experimentController.getPlayerIngredients();
        if (ingredients.size() < 2) {
            view.showInvalidInput("Non hai abbastanza ingredienti per condurre un esperimento.");
            return;
        }
        view.showIngredients(ingredients.stream().map(IngredientDTO::name).toList());

        // 4. Scelta dei due ingredienti
        PotionDTO potion;
        while (true) {
            int firstChoice = view.promptIngredientChoice("Primo ingrediente", ingredients.size());
            int secondChoice;
            do {
                secondChoice = view.promptIngredientChoice("Secondo ingrediente", ingredients.size());
                if (secondChoice == firstChoice) view.showInvalidInput("Devi scegliere due ingredienti diversi.");
            } while (secondChoice == firstChoice);

            try {
                String firstId  = ingredients.get(firstChoice - 1).id();
                String secondId = ingredients.get(secondChoice - 1).id();
                potion = experimentController.conductExperiment(targetId, firstId, secondId);
                break;
            } catch (Exception e) {
                view.showInvalidInput(e.getMessage() + " Riscegli gli ingredienti.");
            }
        }

        // 5. Risultato
        view.showPotionResult(potion.label(), potion.colorName());

        // 6. Aggiornamento facoltativo della griglia di deduzione
        if (view.promptUpdateDeductionGrid()) {
            DeductionGridDTO grid = experimentController.getDeductionGrid();
            view.showDeductionGrid(
                    grid.ingredients().stream().map(IngredientDTO::name).toList(),
                    grid.alchemicLabels(),
                    grid.excluded()
            );

            int ingChoice = view.promptDeductionIngredientChoice(grid.ingredients().size());
            int alcChoice = view.promptDeductionAlchemicChoice(grid.alchemicLabels().size());
            try {
                experimentController.updateDeductionGrid(ingChoice - 1, alcChoice - 1);
                view.showExclusionResult(
                        grid.ingredients().get(ingChoice - 1).name(),
                        "[" + alcChoice + "]"
                );
            } catch (IllegalArgumentException e) {
                view.showInvalidInput("Esclusione non valida: " + e.getMessage());
            }
        }
    }
}
