package alchgame.controller;

import java.util.List;
import java.util.Map;

import alchgame.model.alchemy.*;
import alchgame.model.alchemy.effect.PotionEffectRegistry;
import alchgame.model.player.*;
import alchgame.model.game.*;

/**
 * Controller del caso d'uso "conduci esperimento" (UC09).
 */
public class ExperimentController {

    private final AlchGame alchGame;
    private final AlchemicAlgorithm alchemicAlgorithm;
    private final PotionEffectRegistry effectRegistry;

    public ExperimentController(AlchGame alchGame, AlchemicAlgorithm alchemicAlgorithm, PotionEffectRegistry effectRegistry) {
        this.alchGame = alchGame;
        this.alchemicAlgorithm = alchemicAlgorithm;
        this.effectRegistry = effectRegistry;
    }

    public Map<String, Target> getTargets() {
        return alchGame.getTargets();
    }

    public boolean paymentCheck(String targetId) {
        return alchGame.getTarget(targetId).requiresPayment();
    }

    public int payGold(String targetId) {
        Player player = alchGame.getCurrentRound().getCurrentPlayer();
        Target target = alchGame.getTarget(targetId);
        player.removeGold(target.getPaymentAmount());
        return player.getGold();
    }

    public List<Ingredient> getIngredients() {
        Player player = alchGame.getCurrentRound().getCurrentPlayer();
        // if (!player.canExperiment())
        //     throw new IllegalStateException("Non hai abbastanza ingredienti per condurre un esperimento.");
        return player.getIngredientsFromLab();
    }

    public Potion conductExperiment(String targetId, String ingredientId1, String ingredientId2) {
        Player player = alchGame.getCurrentRound().getCurrentPlayer();
        Ingredient i1 = player.findIngredientById(ingredientId1);
        Ingredient i2 = player.findIngredientById(ingredientId2);
        Target target = alchGame.getTarget(targetId);
        Potion potion = alchemicAlgorithm.computePotion(i1, i2);
        player.updateLab(i1, i2, potion);
        player.publishExperimentResult(potion);
        target.applyEffect(potion, this.effectRegistry);
        return potion;
    }




//-----------------------------------------

    public void updateDeductionGrid(Ingredient ingredient, AlchemicFormula formula) {
        alchGame.getCurrentRound().getCurrentPlayer().excludeFromDeductionGrid(ingredient, formula);
    }

    public DeductionGrid getPlayerDeductionGrid() {
        return alchGame.getCurrentRound().getCurrentPlayer().getDeductionGrid();
    }
}
