package alchgame.service;

import alchgame.model.Experiment;
import alchgame.model.Ingredient;

import java.util.List;

public class StartExperimentResponse {
    private final Experiment experiment;
    private final boolean paymentRequired;
    private final List<Ingredient> availableIngredients;

    public StartExperimentResponse(Experiment experiment, boolean paymentRequired, List<Ingredient> availableIngredients) {
        this.experiment = experiment;
        this.paymentRequired = paymentRequired;
        this.availableIngredients = availableIngredients;
    }

    public static StartExperimentResponse paymentRequired() {
        return new StartExperimentResponse(null, true, List.of());
    }

    public static StartExperimentResponse paymentRequired(Experiment experiment) {
        return new StartExperimentResponse(experiment, true, List.of());
    }

    public static StartExperimentResponse paymentNotRequired() {
        return new StartExperimentResponse(null, false, List.of());
    }

    public static StartExperimentResponse ready(Experiment experiment, List<Ingredient> availableIngredients) {
        return new StartExperimentResponse(experiment, false, availableIngredients);
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
}
