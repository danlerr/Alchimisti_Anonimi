package alchgame.model;

/**
 * Represents a target for an alchemical experiment.
 * This interface applies the POLYMORPHISM GRASP principle to handle different
 * experiment targets without conditional logic.
 */
public interface ExperimentTarget {
    
    /**
     * Determines if this target requires payment before conducting the experiment.
     * @return true if payment is required, false otherwise
     */
    boolean requiresPayment();
    
    /**
     * Applies the consequences of the experiment based on the potion result.
     * @param potion the resulting potion from the experiment
     * @param player the player conducting the experiment
     */
    void applyConsequences(Potion potion, Player player);
    
    /**
     * Returns a human-readable description of this target.
     * @return description of the target
     */
    String getDescription();
    
    /**
     * Returns the type identifier for this target.
     * @return the target type
     */
    TargetType getType();
}
