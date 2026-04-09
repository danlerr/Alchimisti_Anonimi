package alchgame.controller;

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

    public ExperimentStep startExperiment(String targetId) {
        this.currentTarget = gameContext.getTarget(targetId);
        Player player = gameContext.getCurrentPlayer();
        boolean payment = currentTarget.requiresPayment();

        if (payment) {
            return new PaymentRequest();
        } else {
            return new IngredientsRequest(player.getIngredientsFromLab());
        }
    }

    public IngredientsRequest pagaOro() {
        Player player = gameContext.getCurrentPlayer();
        boolean success = player.removeGold(1);
        if (!success) throw new IllegalStateException("Oro insufficiente.");
        return new IngredientsRequest(player.getIngredientsFromLab());
    }

    public Experiment conductExperiment(Ingredient ingredient1, Ingredient ingredient2) {
        if (ingredient1.equals(ingredient2))
            throw new IllegalArgumentException("Gli ingredienti devono essere distinti.");
        Potion potion = alchemicAlgorithm.computePotion(ingredient1, ingredient2);
        Experiment experiment = Experiment.createExperiment(
                currentTarget, ingredient1, ingredient2, potion);
        Player player = gameContext.getCurrentPlayer();
        player.publishExperimentResult(potion);
        player.updateLab(ingredient1, ingredient2, potion);
        currentTarget.applyEffect(potion);
        player.addExperiment(experiment);
        return experiment;
    }

    public void rinunciaEsperimento() {
        this.currentTarget = null;
    }
}
