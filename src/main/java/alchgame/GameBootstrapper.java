package alchgame;

import alchgame.controller.ExperimentHandler;
import alchgame.controller.StartGameHandler;
import alchgame.controller.TurnHandler;
import alchgame.model.*;
import alchgame.service.AlchGame;
import alchgame.service.AlchemicAlgorithm;
import alchgame.service.AlchemicMapping;
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

        AlchemicMapping alchemicMapping = new AlchemicMapping(rawMapping);
        Student         student         = new Student();
        Board           board           = buildBoard(ingredients);

        AlchGame alchGame = new AlchGame(
                board,
                ingredients,
                formulas,
                alchemicMapping,
                Map.of(GameConfig.TARGET_STUDENT_ID, student),
                GameConfig.TARGET_SELF_ID);

        StartGameHandler  startHandler      = new StartGameHandler(alchGame);
        TurnHandler       turnHandler       = new TurnHandler(alchGame);
        ExperimentHandler experimentHandler = new ExperimentHandler(alchGame, new AlchemicAlgorithm(alchemicMapping));
        GameView          view              = new GameView();

        new GamePresenter(alchGame, experimentHandler, turnHandler, startHandler, view).run();
    }

    private static Board buildBoard(List<Ingredient> ingredients) {
        Map<String, Slot> slots = new HashMap<>();
        for (GameConfig.SlotSpec spec : GameConfig.SLOTS) {
            slots.put(spec.id(), new Slot(spec.id(), new Resources(spec.ingredientCount(), spec.favorCount())));
        }

        List<String> slotOrder = GameConfig.SLOTS.stream()
                .map(GameConfig.SlotSpec::id)
                .toList();
        OrderSpace orderSpace = new OrderSpace(slots, slotOrder);

        Map<String, ActionSpace> actionSpaces = new HashMap<>();
        for (String id : GameConfig.ACTION_ORDER)
            actionSpaces.put(id, new ActionSpace(id));

        List<Ingredient> ingredientDeckList = new ArrayList<>();
        for (int copy = 0; copy < GameConfig.INGREDIENT_DECK_COPIES; copy++) {
            for (Ingredient ingredient : ingredients)
                ingredientDeckList.add(new Ingredient(ingredient.getName()));
        }
        Collections.shuffle(ingredientDeckList);
        Deque<Ingredient> ingredientDeck = new ArrayDeque<>(ingredientDeckList);

        Deque<Favor> favorDeck = new ArrayDeque<>();
        for (int i = 0; i < GameConfig.FAVOR_DECK_SIZE; i++)
            favorDeck.add(new Favor("favor-" + i));

        return new Board(actionSpaces, orderSpace, ingredientDeck, favorDeck);
    }
}
