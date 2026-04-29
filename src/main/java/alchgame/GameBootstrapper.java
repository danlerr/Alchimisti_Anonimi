package alchgame;

import alchgame.controller.ExperimentController;
import alchgame.controller.StartGameController;
import alchgame.controller.TurnController;
import alchgame.model.alchemy.*;
import alchgame.model.board.*;

import alchgame.model.game.*;
import alchgame.model.game.GameSession;
import alchgame.service.AlchemicAlgorithm;
import alchgame.service.AlchemicMapping;
import alchgame.service.GameFlowService;
import alchgame.service.GameSetupService;
import alchgame.service.PlayerFactory;
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
        PlayerFactory   playerFactory   = new PlayerFactory(ingredients, formulas);

        GameSession alchGame = new GameSession(
                board,
                ingredients,
                formulas,
                Map.of(GameConfig.TARGET_STUDENT_ID, student),
                GameConfig.TARGET_SELF_ID,
                GameConfig.STARTING_ACTION_CUBES,
                GameConfig.TOTAL_ROUNDS);

        GameSetupService gameSetupService = new GameSetupService(
                alchGame,
                playerFactory,
                new Random(),
                GameConfig.MIN_PLAYERS,
                GameConfig.MAX_PLAYERS,
                GameConfig.STARTING_GOLD,
                GameConfig.STARTING_REPUTATION,
                GameConfig.STARTING_ACTION_CUBES,
                GameConfig.STARTING_INGREDIENTS);

        GameFlowService gameFlowService = new GameFlowService(alchGame);

        StartGameController  startHandler      = new StartGameController(gameSetupService);
        TurnController       turnHandler       = new TurnController(alchGame);
        ExperimentController experimentHandler = new ExperimentController(alchGame, new AlchemicAlgorithm(alchemicMapping));
        GameView          view              = new GameView();

        new GamePresenter(alchGame, gameFlowService, experimentHandler, turnHandler, startHandler, view).run();
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

        return new Board(actionSpaces, orderSpace, new CardDeck<>(ingredientDeck), new CardDeck<>(favorDeck));
    }
}
