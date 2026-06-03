package alchgame.application;

import java.util.List;
import java.util.Map;

import java.util.function.Supplier;
import alchgame.application.observer.*;
import alchgame.model.alchemy.*;
import alchgame.model.alchemy.effect.PotionEffectRegistry;
import alchgame.model.player.*;
import alchgame.model.game.*;

/**
 * Controller del caso d'uso "conduci esperimento" (UC09).
 */
public class ExperimentController extends ActionSubject {

    private final Supplier<Round> round;
    private final AlchemicAlgorithm alchemicAlgorithm;
    private final PotionEffectRegistry effectRegistry;

    public ExperimentController(Supplier<Round> round, AlchemicAlgorithm alchemicAlgorithm, PotionEffectRegistry effectRegistry) {
        this.round = round;
        this.alchemicAlgorithm = alchemicAlgorithm;
        this.effectRegistry = effectRegistry;
    }

    public Map<String, Target> getTargets() {
        return round.get().getTargets();
    }

    public boolean paymentCheck(String targetId) {
        return round.get().getTarget(targetId).requiresPayment();
    }

    public int payGold(String targetId) {
        Player player = round.get().getCurrentPlayer();
        Target target = round.get().getTarget(targetId);
        player.removeGold(target.getPaymentAmount());
        return player.getGold();
    }

    public Potion conductExperiment(String targetId, String ingredientId1, String ingredientId2) {
        Player player = round.get().getCurrentPlayer();
        Ingredient i1 = player.findIngredientById(ingredientId1);
        Ingredient i2 = player.findIngredientById(ingredientId2);
        Target target = round.get().getTarget(targetId);
        Potion potion = alchemicAlgorithm.computePotion(i1, i2);
        player.updateLab(i1, i2, potion);
        player.publishExperimentResult(potion);
        target.applyEffect(potion, this.effectRegistry);
        notifyObservers();
        return potion;
        
    }




//-----------------------------------------

    //metodo da tolgiere, NON è un command, ma una query -> il layer presenter può chiamare chiamare direttamente il model 
    public List<Ingredient> getIngredients() {
        Player player = round.get().getCurrentPlayer();
        // if (!player.canExperiment())
        //     throw new IllegalStateException("Non hai abbastanza ingredienti per condurre un esperimento.");
        return player.getIngredientsFromLab();
    }

    
    public void updateDeductionGrid(Ingredient ingredient, AlchemicFormula formula) {
        round.get().getCurrentPlayer().excludeFromDeductionGrid(ingredient, formula);
    }

    public DeductionGrid getPlayerDeductionGrid() {
        return round.get().getCurrentPlayer().getDeductionGrid();
    }
}
