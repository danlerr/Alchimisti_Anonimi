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

    public Potion conductExperiment(String targetId, int firstIdx, int secondIdx) {
        Player player = alchGame.getCurrentRound().getCurrentPlayer();
        Ingredient i1 = player.getIngredientsFromLab().get(firstIdx);
        Ingredient i2 = player.getIngredientsFromLab().get(secondIdx);
        Target target = alchGame.getTarget(targetId);
        Potion potion = alchemicAlgorithm.computePotion(i1, i2);
        player.updateLab(i1, i2, potion);
        player.publishExperimentResult(potion);
        target.applyEffect(potion);
        return potion;
    }

    public List<Ingredient> getPlayerIngredients() {
        return alchGame.getCurrentRound().getCurrentPlayer().getIngredientsFromLab();
    }
    
    public DeductionGrid getPlayerDeductionGrid() {
        return alchGame.getCurrentRound().getCurrentPlayer().getDeductionGrid();
    }

    public void updateDeductionGrid(int ingredientIndex, int alchemicIndex) {
        alchGame.getCurrentRound().getCurrentPlayer().excludeFromDeductionGrid(ingredientIndex, alchemicIndex);
    }
}
