package alchgame.controller;


import java.util.List;
import java.util.function.Supplier;


import alchgame.resources.GameConfig;
import alchgame.model.alchemy.*;
import alchgame.model.player.*;
import alchgame.model.game.*;

public class ExperimentController {

    private final Supplier<Round> round;
    private final Target student;
    private final AlchemicAlgorithm  alchemicAlgorithm;

    public ExperimentController(Supplier<Round> round, Target student, AlchemicAlgorithm alchemicAlgorithm) {
        this.round = round;
        this.student = student;
        this.alchemicAlgorithm = alchemicAlgorithm;
    }
   public boolean paymentCheck(String targetId) {
        return resolveTarget(targetId).requiresPayment();
    }
     public DeductionGrid getDeductionGrid() {
        return round.get().getCurrentPlayer().getDeductionGrid();
    }
    public int payGold() {
        Round t = round.get();
        t.getCurrentPlayer().removeGold(1);
        return t.getCurrentPlayer().getGold();
    }
    public Potion conductExperiment(String targetId, int firstIdx, int secondIdx) {
        List<Ingredient> available = round.get().getCurrentPlayer().getIngredientsFromLab();
        return conductExperiment(targetId, available.get(firstIdx), available.get(secondIdx));
    }
    private Potion conductExperiment(String targetId, Ingredient i1, Ingredient i2) {
        Round t = round.get();
        Target target  = resolveTarget(targetId);
        Player player  = t.getCurrentPlayer();
        Potion potion  = alchemicAlgorithm.computePotion(i1, i2);
        player.updateLab(i1, i2, potion);
        player.publishExperimentResult(potion);
        target.applyEffect(potion);
        return potion;
    }
    public void updateDeductionGrid(int ingredientIndex, int alchemicIndex) {
        round.get().getCurrentPlayer().excludeFromDeductionGrid(ingredientIndex, alchemicIndex);
    }

    private Target resolveTarget(String targetId) {
        if (GameConfig.SELF_ID.equals(targetId)) return round.get().getCurrentPlayer();
        if (GameConfig.TARGET_STUDENT_ID.equals(targetId)) return student;
        throw new IllegalArgumentException("Target non valido: " + targetId);
    }

}
