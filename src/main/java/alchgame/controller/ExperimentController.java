package alchgame.controller;

import java.util.List;

import alchgame.model.alchemy.*;

import alchgame.model.player.*;
import alchgame.model.game.*;
import alchgame.model.game.GameSession;
import alchgame.service.AlchemicAlgorithm;

/**
 * ExperimentHandler - UC08 Controller class
 */
public class ExperimentController {

    private final GameSession game;
    private final AlchemicAlgorithm alchemicAlgorithm;

    public ExperimentController(GameSession game, AlchemicAlgorithm alchemicAlgorithm) {
        this.game = game;
        this.alchemicAlgorithm = alchemicAlgorithm;
    }

    public boolean paymentCheck(String targetId) {
        return game.getTarget(targetId).requiresPayment();
    }

    public List<Ingredient> getIngredients() {
        return game.getCurrentPlayer().getIngredientsFromLab();
    }

    public DeductionGrid getDeductionGrid() {
        return game.getCurrentPlayer().getPrivateLaboratory().getDeductionGrid();
    }

    public void payGold() {
        game.getCurrentPlayer().removeGold(1);
    }

    public Potion conductExperiment(String targetId, Ingredient i1, Ingredient i2) {
        Target target = game.getTarget(targetId);
        Player player = game.getCurrentPlayer();
        Potion potion = alchemicAlgorithm.computePotion(i1, i2);
        player.updateLab(i1, i2, potion);
        player.publishExperimentResult(potion);
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
