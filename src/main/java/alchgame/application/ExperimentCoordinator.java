package alchgame.application;

import alchgame.model.*;

/**
 * Coordinates the execution of alchemical experiments.
 * Applies PURE FABRICATION - not a domain concept, but improves design.
 * Applies HIGH COHESION - all methods relate to conducting experiments.
 * Applies LOW COUPLING - depends only on essential domain objects.
 */
public class ExperimentCoordinator {
    
    private final AlchemicAlgorithm algorithm;
    
    public ExperimentCoordinator(AlchemicAlgorithm algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException("AlchemicAlgorithm cannot be null");
        }
        this.algorithm = algorithm;
    }
    
    /**
     * Conducts an experiment and updates all relevant game state.
     * Applies INFORMATION EXPERT - delegates to objects that own their data.
     * 
     * @param experiment the experiment being conducted
     * @param ingredient1 first ingredient
     * @param ingredient2 second ingredient
     * @param laboratory the player's private laboratory
     * @param playerBoard the player's public board
     * @return the result of the experiment
     */
    public ExperimentResult conduct(
            Experiment experiment,
            Ingredient ingredient1,
            Ingredient ingredient2,
            PrivateLaboratory laboratory,
            PublicPlayerBoard playerBoard) {
        
        validateInputs(experiment, ingredient1, ingredient2, laboratory, playerBoard);
        
        // Compute the resulting potion using the alchemic algorithm
        Potion potion = algorithm.computePotion(ingredient1, ingredient2);
        
        // Update experiment state
        experiment.complete(ingredient1, ingredient2, potion);
        
        // Update laboratory (private information)
        laboratory.updateLab(ingredient1, ingredient2, potion);
        
        // Update public board (visible to all players)
        playerBoard.publishResult(potion);
        
        return new ExperimentResult(experiment, potion);
    }
    
    private void validateInputs(
            Experiment experiment,
            Ingredient ingredient1,
            Ingredient ingredient2,
            PrivateLaboratory laboratory,
            PublicPlayerBoard playerBoard) {
        
        if (experiment == null) {
            throw new IllegalArgumentException("Experiment cannot be null");
        }
        if (ingredient1 == null || ingredient2 == null) {
            throw new IllegalArgumentException("Ingredients cannot be null");
        }
        if (ingredient1.equals(ingredient2)) {
            throw new IllegalArgumentException("Cannot use the same ingredient twice");
        }
        if (laboratory == null) {
            throw new IllegalArgumentException("Laboratory cannot be null");
        }
        if (playerBoard == null) {
            throw new IllegalArgumentException("PlayerBoard cannot be null");
        }
    }
}
