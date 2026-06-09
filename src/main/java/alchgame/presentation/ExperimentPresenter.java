package alchgame.presentation;

import alchgame.application.ExperimentController;
import alchgame.application.dto.DeductionGridDTO;
import alchgame.application.dto.IngredientDTO;
import alchgame.application.dto.PotionDTO;

import java.util.ArrayList;
import java.util.List;

public class ExperimentPresenter {

    private final GameView view;
    private final ExperimentController experimentController;

    public ExperimentPresenter(GameView view, ExperimentController experimentController) {
        this.view = view;
        this.experimentController = experimentController;
    }

    public void run() {
        // 0. Pre-condizione: servono almeno due ingredienti distinti. Se mancano,
        //    salta l'esperimento ma fa comunque avanzare il turno (altrimenti il
        //    game loop resta senza eventi e l'applicazione sembra "uscire").
        List<IngredientDTO> ingredients = experimentController.getPlayerIngredients();
        if (ingredients.size() < 2
                || ingredients.stream().map(IngredientDTO::name).distinct().count() < 2) {
            view.showInvalidInput("Non hai due ingredienti distinti: esperimento saltato.");
            experimentController.skipExperiment();
            return;
        }

        // 1. Scelta bersaglio
        List<String> targetIds = new ArrayList<>(experimentController.getTargetIds());
        view.showTargetOptions(targetIds);
        String targetId = view.promptTargetChoice(targetIds);

        // 2. Pagamento se richiesto
        boolean targetWasUnhappy = experimentController.paymentCheck(targetId);
        if (targetWasUnhappy) {
            view.showPaymentRequired();
            try {
                int remaining = experimentController.payGold(targetId);
                view.showPaymentResult(remaining);
            } catch (IllegalStateException e) {
                view.showInsufficientGold();
                experimentController.skipExperiment();
                return;
            }
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
                IngredientDTO first = ingredients.get(firstChoice - 1);
                IngredientDTO second = ingredients.get(secondChoice - 1);
                if (first.name().equals(second.name())) {
                    view.showInvalidInput("Gli ingredienti devono essere distinti. Riscegli gli ingredienti.");
                    continue;
                }
                potion = experimentController.conductExperiment(targetId, first.id(), second.id());
                break;
            } catch (Exception e) {
                view.showInvalidInput(e.getMessage() + " Riscegli gli ingredienti.");
            }
        }

        // 5. Risultato
        view.showPotionResult(potion.label(), potion.colorName());
        if (experimentController.isCurrentPlayerTarget(targetId)) {
            view.showPotionEffect(potion.label(), potion.colorName());
        }
        if (!targetWasUnhappy && experimentController.paymentCheck(targetId)) {
            view.showStudentPoisoned();
        }

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

        // 7. Notifica fine esperimento — DOPO l'aggiornamento griglia, così il
        //    GameStateDTO assemblato riflette già l'esclusione appena applicata.
        experimentController.endExperiment();
    }
}
