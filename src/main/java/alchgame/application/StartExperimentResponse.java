package alchgame.application;

import alchgame.model.Experiment;
import alchgame.model.Ingredient;

import java.util.List;

public class StartExperimentResponse {
    private final Experiment experiment;
    private final boolean paymentRequired;
    private final List<Ingredient> availableIngredients;
    private final String message;
    private final int paymentCost;

    private StartExperimentResponse(Experiment experiment, boolean paymentRequired, 
                                   List<Ingredient> availableIngredients, String message, int paymentCost) {
        this.experiment = experiment;
        this.paymentRequired = paymentRequired;
        this.availableIngredients = availableIngredients;
        this.message = message;
        this.paymentCost = paymentCost;
    }

    public static StartExperimentResponse paymentRequired(Experiment experiment, int cost) {
        return new StartExperimentResponse(
            experiment, 
            true, 
            List.of(), 
            "Payment of " + cost + " gold required",
            cost
        );
    }

    public static StartExperimentResponse ready(Experiment experiment, List<Ingredient> availableIngredients) {
        return new StartExperimentResponse(
            experiment, 
            false, 
            availableIngredients,
            "Experiment ready to conduct",
            0
        );
    }
    
    public static StartExperimentResponse noActiveExperiment() {
        return new StartExperimentResponse(
            null,
            false,
            List.of(),
            "No active experiment",
            0
        );
    }
    
    public static StartExperimentResponse paymentFailed(String reason) {
        return new StartExperimentResponse(
            null,
            true,
            List.of(),
            "Payment failed: " + reason,
            0
        );
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public boolean isPaymentRequired() {
        return paymentRequired;
    }

    public List<Ingredient> getAvailableIngredients() {
        return availableIngredients;
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getPaymentCost() {
        return paymentCost;
    }
}
