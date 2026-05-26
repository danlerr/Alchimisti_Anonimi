package alchgame.controller;

import java.util.List;
import java.util.Map;

import alchgame.model.alchemy.*;
import alchgame.model.player.*;
import alchgame.model.game.*;

public class ExperimentController {

    private final AlchGame alchGame;
    private final AlchemicAlgorithm alchemicAlgorithm;

    public ExperimentController(AlchGame alchGame, AlchemicAlgorithm alchemicAlgorithm) {
        this.alchGame = alchGame;
        this.alchemicAlgorithm = alchemicAlgorithm;
    }

    public Map<String, Target> getAvailableTargets() {
        return alchGame.getAvailableTargets();
    }

    public boolean paymentCheck(String targetId) {
        return alchGame.getTarget(targetId).requiresPayment();
    }

    public int payGold() {
        Player player = alchGame.getCurrentRound().getCurrentPlayer();
        player.removeGold(1);
        return player.getGold();
    }

    public Potion conductExperiment(String targetId, String firstId, String secondId) {
        Player player = alchGame.getCurrentRound().getCurrentPlayer();
        Ingredient i1 = player.findIngredientInLabById(firstId);
        Ingredient i2 = player.findIngredientInLabById(secondId);
        Target target = alchGame.getTarget(targetId);
        Potion potion = alchemicAlgorithm.computePotion(i1, i2);
        player.updateLab(i1, i2, potion);
        player.publishExperimentResult(potion);
        target.applyEffect(potion);
        return potion;
    }

    public List<Ingredient> getPlayerIngredients() {
        Player player = alchGame.getCurrentRound().getCurrentPlayer();
        if (!player.canExperiment())
            throw new IllegalStateException("Non hai abbastanza ingredienti per condurre un esperimento.");
        return player.getIngredientsFromLab();
    }
    
    public DeductionGrid getPlayerDeductionGrid() {
        return alchGame.getCurrentRound().getCurrentPlayer().getDeductionGrid();
    }

    public void updateDeductionGrid(Ingredient ingredient, AlchemicFormula formula) {
        alchGame.getCurrentRound().getCurrentPlayer().excludeFromDeductionGrid(ingredient, formula);
    }
}
