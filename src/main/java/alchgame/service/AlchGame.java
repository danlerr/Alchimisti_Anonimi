package alchgame.service;

import alchgame.model.Experiment;
import alchgame.model.Ingredient;
import alchgame.model.Player;

public class AlchGame {
    private final GameEngine gameEngine;

    public AlchGame(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public Player getCurrentPlayer() {
        return gameEngine.getCurrentPlayer();
    }

    public Experiment getCurrentExperiment() {
        return gameEngine.getCurrentExperiment();
    }

    public StartExperimentResponse startExperiment(Target target) {
        return gameEngine.startExperiment(target);
    }

    public StartExperimentResponse payGold() {
        return gameEngine.payGold();
    }

    public void renounceExperiment() {
        gameEngine.renounceExperiment();
    }

    public ConductExperimentResponse conductExperiment(Ingredient ingredient1, Ingredient ingredient2) {
        return gameEngine.conductExperiment(ingredient1, ingredient2);
    }
}
