package alchgame.application;

import alchgame.model.*;

import java.util.List;

/**
 * Main game engine coordinating workflows.
 */
public class GameEngine {
    
    private final GameContext context;
    private final Player currentPlayer;
    private final PaymentService paymentService;
    private final ExperimentCoordinator experimentCoordinator;
    
    private Experiment currentExperiment;
    
    /**
     * Creates a new game engine.
     * 
     * @param context aggregated game components
     * @param currentPlayer the current player
     * @param paymentService service for handling gold payments
     * @param experimentCoordinator service for conducting experiments
     */
    public GameEngine(
            GameContext context,
            Player currentPlayer,
            PaymentService paymentService,
            ExperimentCoordinator experimentCoordinator) {
        
        if (context == null) {
            throw new IllegalArgumentException("GameContext cannot be null");
        }
        if (currentPlayer == null) {
            throw new IllegalArgumentException("Current player cannot be null");
        }
        if (paymentService == null) {
            throw new IllegalArgumentException("PaymentService cannot be null");
        }
        if (experimentCoordinator == null) {
            throw new IllegalArgumentException("ExperimentCoordinator cannot be null");
        }
        
        this.context = context;
        this.currentPlayer = currentPlayer;
        this.paymentService = paymentService;
        this.experimentCoordinator = experimentCoordinator;
    }
    
    /**
     * Starts a new experiment with the given target.
     * 
     * @param target the experiment target
     * @return response indicating if payment is required or experiment is ready to conduct
     */
    public StartExperimentResponse startExperiment(ExperimentTarget target) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        
        // Create new experiment (CREATOR pattern)
        currentExperiment = new Experiment(currentPlayer, target);
        
        // Check if payment is required 
        if (target.requiresPayment()) {
            int cost = paymentService.calculateCost(target);
            return StartExperimentResponse.paymentRequired(currentExperiment, cost);
        }
        
        // Experiment is ready - provide available ingredients
        List<Ingredient> ingredients = context.getLaboratory().getIngredients();
        return StartExperimentResponse.ready(currentExperiment, ingredients);
    }
    
    /**
     * Processes payment for the current experiment.
     * Delegates to PaymentService.
     * 
     * @return response indicating payment result
     */
    public StartExperimentResponse payGold() {
        if (currentExperiment == null) {
            return StartExperimentResponse.noActiveExperiment();
        }
        
        // Delegate payment processing to specialized service
        PaymentResult paymentResult = paymentService.processPayment(
            currentPlayer,
            currentExperiment.getTarget()
        );
        
        if (!paymentResult.isSuccess()) {
            return StartExperimentResponse.paymentFailed(paymentResult.getMessage());
        }
        
        // Mark payment as satisfied in experiment
        currentExperiment.markPaymentSatisfied();
        
        // Now experiment is ready
        List<Ingredient> ingredients = context.getLaboratory().getIngredients();
        return StartExperimentResponse.ready(currentExperiment, ingredients);
    }
    
    /**
     * Cancels the current experiment.
     * Experiment manages its own state.
     */
    public void cancelExperiment() {
        if (currentExperiment != null) {
            currentExperiment.cancel();
            currentExperiment = null;
        }
    }
    
    /**
     * Conducts the experiment with the given ingredients.
     * Delegates to ExperimentCoordinator.
     * 
     * @param ingredient1 first ingredient
     * @param ingredient2 second ingredient
     * @return response with the resulting potion
     */
    public ConductExperimentResponse conductExperiment(
            Ingredient ingredient1,
            Ingredient ingredient2) {
        
        if (currentExperiment == null) {
            return ConductExperimentResponse.noActiveExperiment();
        }
        
        if (!currentExperiment.isReadyToConduct()) {
            return ConductExperimentResponse.notReady(
                "Experiment not ready: " + currentExperiment.getPaymentStatus()
            );
        }
        
        // Delegate to specialized coordinator
        ExperimentResult result = experimentCoordinator.conduct(
            currentExperiment,
            ingredient1,
            ingredient2,
            context.getLaboratory(),
            context.getPlayerBoard()
        );
        
        // Apply consequences
        currentExperiment.getTarget().applyConsequences(
            result.getPotion(),
            currentPlayer
        );
        
        return ConductExperimentResponse.success(result.getPotion());
    }
    
    /**
     * Creates an experiment target based on target type.
     * 
     * @param targetType the type of target
     * @return the created target
     */
    public ExperimentTarget createTarget(TargetType targetType) {
        if (targetType == null) {
            throw new IllegalArgumentException("TargetType cannot be null");
        }
        
        switch (targetType) {
            case STUDENT:
                return new StudentTarget(context.getStudent());
            case SELF:
                return new SelfTarget();
            default:
                throw new IllegalArgumentException("Unknown target type: " + targetType);
        }
    }
    
    // Getters
    
    public Experiment getCurrentExperiment() {
        return currentExperiment;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public GameContext getContext() {
        return context;
    }
}
