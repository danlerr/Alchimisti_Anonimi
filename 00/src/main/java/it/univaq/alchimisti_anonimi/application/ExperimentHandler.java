package it.univaq.alchimisti_anonimi.application;

import it.univaq.alchimisti_anonimi.domain.Experiment;
import it.univaq.alchimisti_anonimi.domain.ExperimentTarget;
import it.univaq.alchimisti_anonimi.domain.IngredientCard;
import it.univaq.alchimisti_anonimi.domain.Potion;
import it.univaq.alchimisti_anonimi.domain.PrivateLaboratory;
import it.univaq.alchimisti_anonimi.domain.PublicPlayerBoard;
import it.univaq.alchimisti_anonimi.game.GameEngine;

import java.util.List;
import java.util.Objects;

public class ExperimentHandler {
    private final GameEngine gameEngine;
    private Experiment currentExperiment;

    public ExperimentHandler(GameEngine gameEngine) {
        this.gameEngine = Objects.requireNonNull(gameEngine);
    }

    public StartExperimentResult startExperiment(ExperimentTarget target) {
        Experiment experiment = gameEngine.startExperiment(target, gameEngine.getCurrentPlayer());
        this.currentExperiment = experiment;
        if (experiment.isPaymentRequired()) {
            return StartExperimentResult.paymentRequired();
        }
        return StartExperimentResult.withIngredients(getIngredients());
    }

    public List<IngredientCard> payGold() {
        ensureExperimentAvailable();
        if (!currentExperiment.isPaymentRequired()) {
            return getIngredients();
        }
        gameEngine.getCurrentPlayer().removeGold(1);
        currentExperiment.setPaymentSatisfied(true);
        return getIngredients();
    }

    public Potion conductExperiment(String ingredientId1, String ingredientId2) {
        ensureExperimentAvailable();
        if (currentExperiment.isPaymentRequired() && !currentExperiment.isPaymentSatisfied()) {
            throw new IllegalStateException("Payment required before conducting experiment");
        }
        PrivateLaboratory lab = gameEngine.getPrivateLaboratory();
        IngredientCard ingredient1 = lab.getIngredientCard(ingredientId1);
        IngredientCard ingredient2 = lab.getIngredientCard(ingredientId2);
        Potion potion = gameEngine.getAlchemicAlgorithm().computePotion(ingredient1, ingredient2);
        currentExperiment.completeExperiment(ingredient1, ingredient2, potion);
        PublicPlayerBoard board = gameEngine.getPublicPlayerBoard();
        board.publishResult(potion);
        lab.updateLab(ingredient1, ingredient2, potion);
        return potion;
    }

    public boolean cancelExperiment() {
        ensureExperimentAvailable();
        currentExperiment.setCancelled(true);
        clearCurrentExperiment();
        return true;
    }

    public Experiment getCurrentExperiment() {
        return currentExperiment;
    }

    private void clearCurrentExperiment() {
        this.currentExperiment = null;
    }

    private List<IngredientCard> getIngredients() {
        return gameEngine.getPrivateLaboratory().getIngredients();
    }

    private void ensureExperimentAvailable() {
        if (currentExperiment == null) {
            throw new IllegalStateException("No experiment in progress");
        }
    }
}
