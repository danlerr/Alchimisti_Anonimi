package alchgame.controller;

import alchgame.model.*;
import alchgame.service.AlchemicAlgorithm;
import alchgame.service.GameContext;

import java.util.List;

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

    public Object startExperiment(String targetId) {
        this.currentTarget = gameContext.getTarget(targetId);
        Player player = gameContext.getCurrentPlayer();
        boolean payment = currentTarget.requiresPayment();

        if (payment) {
            return new PaymentRequest();
        } else {
            PrivateLaboratory lab = player.getPrivateLaboratory();
            List<Ingredient> ingredients = lab.getIngredients();
            return new IngredientsRequest(ingredients);
        }
    }

    public IngredientsRequest pagaOro() {
        Player player = gameContext.getCurrentPlayer();
        boolean success = player.removeGold(1);
        if (!success) throw new IllegalStateException("Oro insufficiente.");
        PrivateLaboratory privateLaboratory = player.getPrivateLaboratory();
        List<Ingredient> ingredients = privateLaboratory.getIngredients();
        return new IngredientsRequest(ingredients);
    }

    public void conductExperiment(Ingredient ingredient1, Ingredient ingredient2) {
        if (ingredient1.equals(ingredient2))
            throw new IllegalArgumentException("Gli ingredienti devono essere distinti.");
        Potion potion = alchemicAlgorithm.computePotion(ingredient1, ingredient2);
        Experiment experiment = Experiment.createExperiment(
                currentTarget, ingredient1, ingredient2, potion);
        Player player = gameContext.getCurrentPlayer();
        player.getPublicPlayerBoard().publishExperimentResult(potion);
        player.getPrivateLaboratory().updatePrivateLab(ingredient1, ingredient2, potion);
        currentTarget.applyEffect(potion);
        player.addExperiment(experiment);
        showResult(experiment);
    }

    private void showResult(Experiment experiment) {
        System.out.println("[ExperimentHandler] Risultato: " + experiment);
    }
    public void rinunciaEsperimento() {
    this.currentTarget = null;
    System.out.println("[ExperimentHandler] Esperimento rinunciato.");
}
}
