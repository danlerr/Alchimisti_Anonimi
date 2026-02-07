package alchgame.service;

import alchgame.model.Potion;

public class ConductExperimentResponse {
    private final Potion potion;

    public ConductExperimentResponse(Potion potion) {
        this.potion = potion;
    }

    public Potion getPotion() {
        return potion;
    }
}
