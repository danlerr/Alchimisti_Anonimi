package alchgame.application;

import alchgame.model.ExperimentTarget;
import alchgame.model.Player;

/**
 * Service responsible for handling payments for experiments.
 * Applies PURE FABRICATION principle - not a domain concept, but improves
 * LOW COUPLING and HIGH COHESION by separating payment logic.
 */
public class PaymentService {
    
    private static final int STUDENT_EXPERIMENT_COST = 1;
    
    /**
     * Processes payment for an experiment if required.
     * Applies INFORMATION EXPERT - Player knows if it can pay.
     * 
     * @param player the player who must pay
     * @param target the experiment target (determines if payment is needed)
     * @return the result of the payment attempt
     */
    public PaymentResult processPayment(Player player, ExperimentTarget target) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        
        if (!target.requiresPayment()) {
            return PaymentResult.notRequired();
        }
        
        if (!player.canPayGold(STUDENT_EXPERIMENT_COST)) {
            return PaymentResult.insufficientFunds(STUDENT_EXPERIMENT_COST, player.getGold());
        }
        
        player.payGold(STUDENT_EXPERIMENT_COST);
        return PaymentResult.success(STUDENT_EXPERIMENT_COST);
    }
    
    /**
     * Calculates the cost for an experiment with the given target.
     * 
     * @param target the experiment target
     * @return the cost in gold pieces
     */
    public int calculateCost(ExperimentTarget target) {
        if (target == null) {
            return 0;
        }
        return target.requiresPayment() ? STUDENT_EXPERIMENT_COST : 0;
    }
    
    /**
     * Checks if a player can afford an experiment with the given target.
     * 
     * @param player the player
     * @param target the experiment target
     * @return true if the player can afford it, false otherwise
     */
    public boolean canAfford(Player player, ExperimentTarget target) {
        if (!target.requiresPayment()) {
            return true;
        }
        return player.canPayGold(STUDENT_EXPERIMENT_COST);
    }
}
