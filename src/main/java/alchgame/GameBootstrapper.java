package alchgame;

import alchgame.controller.ExperimentHandler;
import alchgame.model.*;
import alchgame.service.AlchemicAlgorithm;
import alchgame.service.AlchemicMapping;
import alchgame.service.GameContext;
import alchgame.view.GamePresenter;
import alchgame.view.GameView;

import java.util.*;

class GameBootstrapper {

    static void run() {
        List<Ingredient> ingredients = GameConfig.INGREDIENT_NAMES.stream()
                .map(Ingredient::new)
                .toList();

        List<AlchemicFormula> formulas = GameConfig.FORMULAS;

        List<AlchemicFormula> shuffled = new ArrayList<>(formulas);
        Collections.shuffle(shuffled);
        Map<Ingredient, AlchemicFormula> rawMapping = new HashMap<>();
        for (int i = 0; i < ingredients.size(); i++)
            rawMapping.put(ingredients.get(i), shuffled.get(i));

        AlchemicMapping alchemicMapping  = new AlchemicMapping(rawMapping);
        DeductionGrid   grid             = new DeductionGrid(ingredients, formulas);
        PrivateLaboratory lab            = new PrivateLaboratory(new ArrayList<>(ingredients), grid, new ResultsTriangle());
        Player          player           = new Player(GameConfig.STARTING_GOLD, GameConfig.STARTING_REPUTATION, lab, new PublicPlayerBoard());
        Student         student          = new Student();

        GameContext gameContext = new GameContext(player, Map.of(
                GameConfig.TARGET_STUDENT_ID, student,
                GameConfig.TARGET_SELF_ID,    player));

        ExperimentHandler handler = new ExperimentHandler(gameContext, new AlchemicAlgorithm(alchemicMapping));

        new GamePresenter(gameContext, handler, new GameView()).start();
    }
}
