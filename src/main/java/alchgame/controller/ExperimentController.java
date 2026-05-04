package alchgame.controller;


import java.util.List;
import java.util.function.Supplier;


import alchgame.model.alchemy.*;
import alchgame.model.player.*;
import alchgame.model.game.*;

public class ExperimentController {

    private final Supplier<Turn> turn;
    private final AlchemicAlgorithm  alchemicAlgorithm;

    public ExperimentController(Supplier<Turn> turn, AlchemicAlgorithm alchemicAlgorithm) {
        this.turn = turn;
        this.alchemicAlgorithm = alchemicAlgorithm;
    }
   public boolean paymentCheck(String targetId) {
        return turn.get().getTarget(targetId).requiresPayment();
    }
     public DeductionGrid getDeductionGrid() {
        return turn.get().getCurrentPlayer().getDeductionGrid();
    }
    public int payGold() {
        Turn t = turn.get();
        t.getCurrentPlayer().removeGold(1);
        return t.getCurrentPlayer().getGold();
    }
    public Potion conductExperiment(String targetId, int firstIdx, int secondIdx) {
        List<Ingredient> available = turn.get().getCurrentPlayer().getIngredientsFromLab();
        return conductExperiment(targetId, available.get(firstIdx), available.get(secondIdx));
    }
    private Potion conductExperiment(String targetId, Ingredient i1, Ingredient i2) {
        Turn t = turn.get();
        Target target  = t.getTarget(targetId);
        Player player  = t.getCurrentPlayer();
        Potion potion  = alchemicAlgorithm.computePotion(i1, i2);
        player.updateLab(i1, i2, potion);
        player.publishExperimentResult(potion);
        target.applyEffect(potion);
        return potion;
    }
    public void updateDeductionGrid(int ingredientIndex, int alchemicIndex) {
        turn.get().getCurrentPlayer().excludeFromDeductionGrid(ingredientIndex, alchemicIndex);
    }

}
