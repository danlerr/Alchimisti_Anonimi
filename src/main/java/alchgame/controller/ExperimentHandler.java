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

    /** Imposta il target e ritorna true se serve pagare oro prima di procedere. */
    public boolean startExperiment(String targetId) {
        this.currentTarget = gameContext.getTarget(targetId);
        return currentTarget.requiresPayment();
    }

    public List<Ingredient> getIngredients() {
        return gameContext.getCurrentPlayer().getIngredientsFromLab();
    }

    public List<Ingredient> pagaOro() {
        Player player = gameContext.getCurrentPlayer();
        boolean success = player.removeGold(1);
        if (!success) throw new IllegalStateException("Oro insufficiente.");     //controllo da spostare
        return player.getIngredientsFromLab();
    }

    public Experiment conductExperiment(Ingredient ingredient1, Ingredient ingredient2) {
        Potion potion = alchemicAlgorithm.computePotion(ingredient1, ingredient2);
        Experiment experiment = Experiment.createExperiment(
                currentTarget, ingredient1, ingredient2, potion);
        Player player = gameContext.getCurrentPlayer();
        player.publishExperimentResult(potion);
        player.updateLab(ingredient1, ingredient2, potion);
        currentTarget.applyEffect(potion);
        //player.addExperiment(currentTarget, ingredient1, ingredient2, potion);   creation patter -> player aggrega esperimento
        player.addExperiment(experiment);
        return experiment;
    }

    public void rinunciaEsperimento() {
        this.currentTarget = null;
    }
}
