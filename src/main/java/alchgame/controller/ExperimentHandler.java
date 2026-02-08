package alchgame.controller;

import alchgame.application.*;
import alchgame.model.*;

/**
UC-08 controller ()
coordinates between UI/external and domain.
 */
public class ExperimentHandler {
    
    private final GameEngine gameEngine;
    
    /**
     * Creates a new experiment handler.
     * 
     * @param gameEngine the game engine to coordinate
     */
    public ExperimentHandler(GameEngine gameEngine) {
        if (gameEngine == null) {
            throw new IllegalArgumentException("GameEngine cannot be null");
        }
        this.gameEngine = gameEngine;
    }
    
    /**
     * Starts a new experiment with the specified target type.
     * 
     * @param targetType the type of target (STUDENT or SELF(player))
     * @return response indicating if payment is required or experiment is ready
     */
    public StartExperimentResponse startExperiment(TargetType targetType) {
        
        validateTargetType(targetType);
        
        // Create target using factory method (CREATOR pattern)
        ExperimentTarget target = gameEngine.createTarget(targetType);
        
        return gameEngine.startExperiment(target);
    }
    
    /**
     * Convenience method for starting experiment with direct target.
     * 
     * @param target the experiment target
     * @return response indicating if payment is required or experiment is ready
     */
    public StartExperimentResponse startExperiment(ExperimentTarget target) {
        validateTarget(target);
        return gameEngine.startExperiment(target);
    }
    
    /**
     * Processes payment for the current experiment.
     * 
     * @return response indicating payment result and experiment readiness
     */
    public StartExperimentResponse payGold() {
        return gameEngine.payGold();
    }
    
    /**
     * Cancels the current experiment.
     */
    public void cancelExperiment() {
        gameEngine.cancelExperiment();
    }
    
    /**
     * Conducts the experiment with the given ingredients.
     * Validates inputs before delegating to game engine.
     * 
     * @param ingredient1 first ingredient to mix
     * @param ingredient2 second ingredient to mix
     * @return response with the resulting potion or error message
     */
    public ConductExperimentResponse conductExperiment(
            Ingredient ingredient1,
            Ingredient ingredient2) {
        
        try {
            validateIngredients(ingredient1, ingredient2);
            return gameEngine.conductExperiment(ingredient1, ingredient2);
        } catch (IllegalArgumentException e) {
            return ConductExperimentResponse.notReady(e.getMessage());
        }
    }
    
    /**
     * Gets the current experiment status.
     * 
     * @return the current experiment, or null if none active
     */
    public Experiment getCurrentExperiment() {
        return gameEngine.getCurrentExperiment();
    }
    
    /**
     * Gets the current player.
     * 
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return gameEngine.getCurrentPlayer();
    }
    
    // Validation methods
    
    private void validateTargetType(TargetType targetType) {
        if (targetType == null) {
            throw new IllegalArgumentException("Target type cannot be null");
        }
    }
    
    private void validateTarget(ExperimentTarget target) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
    }
    
    private void validateIngredients(Ingredient ingredient1, Ingredient ingredient2) {
        if (ingredient1 == null || ingredient2 == null) {
            throw new IllegalArgumentException("Ingredients cannot be null");
        }
        
        if (ingredient1.equals(ingredient2)) {
            throw new IllegalArgumentException(
                "Cannot use the same ingredient twice in an experiment"
            );
        }
    }
}
