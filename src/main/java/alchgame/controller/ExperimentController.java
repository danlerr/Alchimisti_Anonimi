package alchgame.controller;

import java.util.List;
import java.util.function.Supplier;

import alchgame.model.alchemy.*;
import alchgame.model.player.*;
import alchgame.model.game.*;

public class ExperimentController {

    private final Supplier<TurnManager> turnManagerSupplier;
    private final AlchemicAlgorithm     alchemicAlgorithm;

    public ExperimentController(Supplier<TurnManager> turnManagerSupplier, AlchemicAlgorithm alchemicAlgorithm) {
        this.turnManagerSupplier = turnManagerSupplier;
        this.alchemicAlgorithm   = alchemicAlgorithm;
    }

    // ---- query --------------------------------------------------------------

    public boolean paymentCheck(String targetId) {
        return turnManagerSupplier.get().getTarget(targetId).requiresPayment();
    }

    public List<String> getIngredientNames() {
        return turnManagerSupplier.get().getCurrentPlayer()
                .getIngredientsFromLab().stream()
                .map(Ingredient::getName)
                .toList();
    }

    public int getCurrentPlayerGold() {
        return turnManagerSupplier.get().getCurrentPlayer().getGold();
    }

    public int getCurrentPlayerReputation() {
        return turnManagerSupplier.get().getCurrentPlayer().getReputation();
    }

    public String getStudentStateName(String studentTargetId) {
        Target t = turnManagerSupplier.get().getTarget(studentTargetId);
        if (t instanceof Student s) return s.getState().name();
        throw new IllegalArgumentException("Target non è uno Student: " + studentTargetId);
    }

    public DeductionGrid getDeductionGrid() {
        return turnManagerSupplier.get().getCurrentPlayer().getDeductionGrid();
    }

    // ---- commands -----------------------------------------------------------

    public int payGold() {
        TurnManager tm = turnManagerSupplier.get();
        tm.getCurrentPlayer().removeGold(1);
        return tm.getCurrentPlayer().getGold();
    }

    public Potion conductExperiment(String targetId, int firstIdx, int secondIdx) {
        List<Ingredient> available = turnManagerSupplier.get().getCurrentPlayer().getIngredientsFromLab();
        return conductExperiment(targetId, available.get(firstIdx), available.get(secondIdx));
    }

    private Potion conductExperiment(String targetId, Ingredient i1, Ingredient i2) {
        TurnManager tm = turnManagerSupplier.get();
        Target target  = tm.getTarget(targetId);
        Player player  = tm.getCurrentPlayer();
        Potion potion  = alchemicAlgorithm.computePotion(i1, i2);
        player.updateLab(i1, i2, potion);
        player.publishExperimentResult(potion);
        target.applyEffect(potion);
        return potion;
    }

    public void updateDeductionGrid(int ingredientIndex, int alchemicIndex) {
        turnManagerSupplier.get().getCurrentPlayer().excludeFromDeductionGrid(ingredientIndex, alchemicIndex);
    }
}
