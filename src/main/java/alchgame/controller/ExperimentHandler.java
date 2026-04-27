package alchgame.controller;

import java.util.List;

import alchgame.model.*;
import alchgame.service.AlchGame;
import alchgame.service.AlchemicAlgorithm;

/**
 * ExperimentHandler - UC08 Controller class
 */
public class ExperimentHandler {

    private final AlchGame alchGame;
    private final AlchemicAlgorithm alchemicAlgorithm;

    public ExperimentHandler(AlchGame alchGame, AlchemicAlgorithm alchemicAlgorithm) {
        this.alchGame = alchGame;
        this.alchemicAlgorithm = alchemicAlgorithm;
    }

    public boolean paymentCheck(String targetId) {
        return alchGame.getTarget(targetId).requiresPayment();
    }

    public List<Ingredient> getIngredients() {
        return alchGame.getCurrentPlayer().getIngredientsFromLab();
    }

    public DeductionGrid getDeductionGrid() {
        return alchGame.getCurrentPlayer().getPrivateLaboratory().getDeductionGrid();
    }

    public void payGold() {
        alchGame.getCurrentPlayer().removeGold(1);
    }

    public Potion conductExperiment(String targetId, Ingredient i1, Ingredient i2) {
        Target target = alchGame.getTarget(targetId);
        Potion potion = alchemicAlgorithm.computePotion(i1, i2);
        alchGame.getCurrentPlayer().recordExperiment(target, i1, i2, potion);
        target.applyEffect(potion);
        return potion;
    }



    public void updateDeductionGrid(int ingredientIndex, int alchemicIndex) {
        DeductionGrid grid = getDeductionGrid();
        Ingredient ingredient = grid.getIngredients().get(ingredientIndex);
        AlchemicFormula alchemic = grid.getAlchemics().get(alchemicIndex);
        grid.exclude(ingredient, alchemic);
    }
}
