package alchgame.controller;

import alchgame.model.Ingredient;
import alchgame.application.ConductExperimentResponse;
import alchgame.application.GameEngine;
import alchgame.application.StartExperimentResponse;
import alchgame.application.Target;

public class ExperimentHandler {

    public StartExperimentResponse startExperiment(Target target, GameEngine gameEngine) {
        return gameEngine.startExperiment(target);
    }

    public StartExperimentResponse payGold(GameEngine gameEngine) {
        return gameEngine.payGold();
    }

    public void cancelExperiment(GameEngine gameEngine) {
        gameEngine.cancelExperiment();
    }

    public ConductExperimentResponse conductExperiment(Ingredient ingredient1,
                                                       Ingredient ingredient2,
                                                       GameEngine gameEngine) {
        return gameEngine.conductExperiment(ingredient1, ingredient2);
    }
}
