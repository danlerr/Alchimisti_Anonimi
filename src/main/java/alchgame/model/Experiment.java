package alchgame.model;

import java.util.List;

/**
 * Represents an alchemical experiment.
 * Applies INFORMATION EXPERT - manages its own state and lifecycle.
 * Applies HIGH COHESION - all methods relate to experiment state management.
 * Improved version with better encapsulation and domain methods instead of setters.
 */
public class Experiment {
    
    private final Player player;
    private final ExperimentTarget target;
    private PaymentStatus paymentStatus;
    private List<Ingredient> usedIngredients;
    private Potion result;
    private ExperimentStatus status;
    
    /**
     * Creates a new experiment.
     * Applies CREATOR - Player aggregates experiments.
     * 
     * @param player the player conducting the experiment
     * @param target the target of the experiment
     */
    public Experiment(Player player, ExperimentTarget target) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        
        this.player = player;
        this.target = target;
        this.status = ExperimentStatus.IN_ATTESA;
        
        // Initialize payment status based on target requirements
        this.paymentStatus = target.requiresPayment() 
            ? PaymentStatus.REQUIRED 
            : PaymentStatus.NOT_REQUIRED;
    }
    
    /**
     * Marks the payment as satisfied for this experiment.
     * Domain method instead of generic setter - improves encapsulation.
     */
    public void markPaymentSatisfied() {
        if (paymentStatus != PaymentStatus.REQUIRED) {
            throw new IllegalStateException(
                "Cannot mark payment as satisfied when payment status is: " + paymentStatus
            );
        }
        this.paymentStatus = PaymentStatus.SATISFIED;
    }
    
    /**
     * Checks if the experiment is ready to be conducted.
     * Applies INFORMATION EXPERT - Experiment knows its own readiness.
     * 
     * @return true if ready to conduct, false otherwise
     */
    public boolean isReadyToConduct() {
        return status == ExperimentStatus.IN_ATTESA &&
               (paymentStatus == PaymentStatus.NOT_REQUIRED || 
                paymentStatus == PaymentStatus.SATISFIED);
    }
    
    /**
     * Completes the experiment with the given ingredients and result.
     * Domain method that ensures state consistency.
     * 
     * @param ingredient1 first ingredient used
     * @param ingredient2 second ingredient used
     * @param potion the resulting potion
     */
    public void complete(Ingredient ingredient1, Ingredient ingredient2, Potion potion) {
        if (!isReadyToConduct()) {
            throw new IllegalStateException(
                "Experiment is not ready to conduct. Status: " + status + 
                ", Payment: " + paymentStatus
            );
        }
        if (ingredient1 == null || ingredient2 == null) {
            throw new IllegalArgumentException("Ingredients cannot be null");
        }
        if (potion == null) {
            throw new IllegalArgumentException("Potion cannot be null");
        }
        
        this.usedIngredients = List.of(ingredient1, ingredient2);
        this.result = potion;
        this.status = ExperimentStatus.COMPLETATO;
    }
    
    /**
     * Cancels the experiment.
     * Domain method instead of generic setter.
     */
    public void cancel() {
        if (status == ExperimentStatus.COMPLETATO) {
            throw new IllegalStateException("Cannot cancel a completed experiment");
        }
        this.status = ExperimentStatus.ANNULLATO;
    }
    
    // Getters
    
    public Player getPlayer() {
        return player;
    }
    
    public ExperimentTarget getTarget() {
        return target;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public List<Ingredient> getUsedIngredients() {
        return usedIngredients;
    }
    
    public Potion getResult() {
        return result;
    }
    
    public ExperimentStatus getStatus() {
        return status;
    }
    
    public boolean isCancelled() {
        return status == ExperimentStatus.ANNULLATO;
    }
    
    public boolean isCompleted() {
        return status == ExperimentStatus.COMPLETATO;
    }
    
    /**
     * Enumeration for payment status within an experiment.
     */
    public enum PaymentStatus {
        NOT_REQUIRED,   // No payment needed
        REQUIRED,       // Payment needed but not yet paid
        SATISFIED       // Payment has been made
    }
}
