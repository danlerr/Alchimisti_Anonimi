package alchgame.controller;

import java.util.List;

import alchgame.model.*;
import alchgame.service.AlchemicAlgorithm;
import alchgame.service.GameContext;

/**
 * ExperimentHandler - UC08 Controller class
 */
public class ExperimentHandler {

    private final GameContext gameContext;
    private final AlchemicAlgorithm alchemicAlgorithm;
    private Target currentTarget;

    public ExperimentHandler(GameContext gameContext, AlchemicAlgorithm alchemicAlgorithm) {
        this.gameContext = gameContext;
        this.alchemicAlgorithm = alchemicAlgorithm;
    }

    public boolean paymentCheck(String targetId) {
        this.currentTarget = gameContext.getTarget(targetId);
        return currentTarget.requiresPayment();
    }

    public List<Ingredient> getIngredients() {
        return gameContext.getCurrentPlayer().getIngredientsFromLab();
    }

    public void payGold() {
        Player player = gameContext.getCurrentPlayer();
        player.removeGold(1);
    }

    public Potion conductExperiment(Ingredient i1, Ingredient i2) {
        Potion potion = alchemicAlgorithm.computePotion(i1, i2);
        gameContext.getCurrentPlayer().recordExperiment(currentTarget, i1, i2, potion);
        currentTarget.applyEffect(potion);
        return potion;
    }

    public void refuseExperiment() {
        this.currentTarget = null;
    }
}
