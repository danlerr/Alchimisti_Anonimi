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
    private Target currentTarget;

    public ExperimentHandler(AlchGame alchGame, AlchemicAlgorithm alchemicAlgorithm) {
        this.alchGame = alchGame;
        this.alchemicAlgorithm = alchemicAlgorithm;
    }

    public boolean paymentCheck(String targetId) {
        this.currentTarget = alchGame.getTarget(targetId);
        return currentTarget.requiresPayment();
    }

    public List<Ingredient> getIngredients() {
        return alchGame.getCurrentPlayer().getIngredientsFromLab();
    }

    public void payGold() {
        Player player = alchGame.getCurrentPlayer();
        player.removeGold(1);
    }

    public Potion conductExperiment(Ingredient i1, Ingredient i2) {
        Potion potion = alchemicAlgorithm.computePotion(i1, i2);
        alchGame.getCurrentPlayer().recordExperiment(currentTarget, i1, i2, potion);
        currentTarget.applyEffect(potion);
        return potion;
    }

    public void refuseExperiment() {
        this.currentTarget = null;
    }
}
