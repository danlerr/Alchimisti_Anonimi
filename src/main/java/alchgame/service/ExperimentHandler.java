package alchgame.service;

import alchgame.model.Ingredient;

public class ExperimentHandler {

    public StartExperimentResponse startExperiment(Target target, AlchGame alchGame) {
        return alchGame.startExperiment(target);
    }

    public StartExperimentResponse payGold(AlchGame alchGame) {
        return alchGame.payGold();
    }

    public void renounceExperiment(AlchGame alchGame) {
        alchGame.renounceExperiment();
    }

    public ConductExperimentResponse conductExperiment(Ingredient ingredient1,
                                                       Ingredient ingredient2,
                                                       AlchGame alchGame) {
        return alchGame.conductExperiment(ingredient1, ingredient2);
    }
}
