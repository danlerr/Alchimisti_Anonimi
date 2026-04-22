package alchgame;

import alchgame.controller.ExperimentHandler;
import alchgame.controller.TurnHandler;
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

        Board board = buildBoard(ingredients);

        ExperimentHandler experimentHandler = new ExperimentHandler(gameContext, new AlchemicAlgorithm(alchemicMapping));
        @SuppressWarnings("unused")
        TurnHandler turnHandler = new TurnHandler(gameContext, board);

        new GamePresenter(gameContext, experimentHandler, new GameView()).start();
    }

    private static Board buildBoard(List<Ingredient> ingredients) {
        Map<String, Slot> slots = new HashMap<>();
        for (GameConfig.SlotSpec spec : GameConfig.SLOTS) {
            slots.put(spec.id(), new Slot(spec.id(), new Resources(spec.ingredientCount(), spec.favorCount())));
        }
        SpazioOrdine spazioOrdine = new SpazioOrdine(slots);

        List<Ingredient> ingredientDeckList = new ArrayList<>(ingredients);
        Collections.shuffle(ingredientDeckList);
        Deque<Ingredient> ingredientDeck = new ArrayDeque<>(ingredientDeckList);

        Deque<FavorCard> favorDeck = new ArrayDeque<>();
        for (int i = 0; i < GameConfig.FAVOR_DECK_SIZE; i++)
            favorDeck.add(new FavorCard("favor-" + i));

        return new Board(new HashMap<>(), spazioOrdine, ingredientDeck, favorDeck);
    }
}
