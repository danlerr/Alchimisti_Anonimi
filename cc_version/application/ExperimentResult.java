package alchgame.application;

import alchgame.model.Experiment;
import alchgame.model.Potion;

/**
 * Represents the result of conducting an experiment.
 * Encapsulates both the experiment details and the resulting potion.
 */
public class ExperimentResult {
    
    private final Experiment experiment;
    private final Potion potion;
    
    public ExperimentResult(Experiment experiment, Potion potion) {
        if (experiment == null) {
            throw new IllegalArgumentException("Experiment cannot be null");
        }
        if (potion == null) {
            throw new IllegalArgumentException("Potion cannot be null");
        }
        this.experiment = experiment;
        this.potion = potion;
    }
    
    public Experiment getExperiment() {
        return experiment;
    }
    
    public Potion getPotion() {
        return potion;
    }
    
    @Override
    public String toString() {
        return "ExperimentResult{" +
                "experimentStatus=" + experiment.getStatus() +
                ", potion=" + potion.getColor() + " " + potion.getSign() +
                '}';
    }
}
