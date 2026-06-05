package alchgame.application;

import java.util.Map;


import alchgame.application.observer.*;
import alchgame.model.alchemy.*;
import alchgame.model.alchemy.effect.PotionEffectRegistry;
import alchgame.model.player.*;
import alchgame.model.game.*;

/**
 * Controller del caso d'uso "conduci esperimento" (UC09).
 */
public class ExperimentController extends Subject<ActionObserver> {

    private final AlchGame game;
    private final AlchemicAlgorithm alchemicAlgorithm;
    private final PotionEffectRegistry effectRegistry;

    public ExperimentController(AlchGame game, AlchemicAlgorithm alchemicAlgorithm, PotionEffectRegistry effectRegistry) {
        this.game = game;
        this.alchemicAlgorithm = alchemicAlgorithm;
        this.effectRegistry = effectRegistry;
    }

    public Map<String, Target> getTargets() {
        return game.getTargets();
    }

    public boolean paymentCheck(String targetId) {
        return game.getTarget(targetId).requiresPayment();
    }

    public int payGold(String targetId) {
        Player player = game.getCurrentRound().getCurrentPlayer();
        Target target = game.getTarget(targetId);
        player.removeGold(target.getPaymentAmount());
        return player.getGold();
    }

    public Potion conductExperiment(String targetId, String ingredientId1, String ingredientId2) {
        Player player = game.getCurrentRound().getCurrentPlayer();
        Ingredient i1 = player.findIngredientById(ingredientId1);
        Ingredient i2 = player.findIngredientById(ingredientId2);
        Target target = game.getTarget(targetId);
        Potion potion = alchemicAlgorithm.computePotion(i1, i2);
        player.updateLab(i1, i2, potion);
        player.publishExperimentResult(potion);
        target.applyEffect(potion, this.effectRegistry);
        notifyObservers(ActionObserver::onActionCompleted);
        return potion;
    }




    public void updateDeductionGrid(Ingredient ingredient, AlchemicFormula formula) {
        game.getCurrentRound().getCurrentPlayer().excludeFromDeductionGrid(ingredient, formula);
    }

    public DeductionGrid getPlayerDeductionGrid() {
        return game.getCurrentRound().getCurrentPlayer().getDeductionGrid();
    }
}
