package alchgame.application;

import alchgame.model.Potion;

/**
 * Response object for conducting an experiment.
 * Encapsulates the result and any error messages.
 */
public class ConductExperimentResponse {
    
    private final Potion potion;
    private final boolean success;
    private final String message;

    private ConductExperimentResponse(Potion potion, boolean success, String message) {
        this.potion = potion;
        this.success = success;
        this.message = message;
    }

    public static ConductExperimentResponse success(Potion potion) {
        return new ConductExperimentResponse(
            potion,
            true,
            "Experiment conducted successfully"
        );
    }
    
    public static ConductExperimentResponse noActiveExperiment() {
        return new ConductExperimentResponse(
            null,
            false,
            "No active experiment to conduct"
        );
    }
    
    public static ConductExperimentResponse notReady(String reason) {
        return new ConductExperimentResponse(
            null,
            false,
            "Experiment not ready: " + reason
        );
    }

    public Potion getPotion() {
        return potion;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
}
