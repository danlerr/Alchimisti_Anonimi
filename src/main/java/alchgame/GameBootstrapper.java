package alchgame;

import alchgame.controller.ExperimentHandler;
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

        // Dev harness: inietta un singolo player senza passare dal flusso di setup
        // completo (StartGameHandler). In gameplay reale la UI pilota StartGameHandler.
        DeductionGrid grid = new DeductionGrid(ingredients, formulas);
        PrivateLaboratory lab = new PrivateLaboratory(new ArrayList<>(ingredients), grid, new ResultsTriangle());
        Player player = new Player("Alchimista", GameConfig.STARTING_GOLD, GameConfig.STARTING_REPUTATION,
                                   lab, new PublicPlayerBoard());
        alchGame.initializePlayers(List.of(player));

        ExperimentHandler experimentHandler = new ExperimentHandler(alchGame, new AlchemicAlgorithm(alchemicMapping));
        @SuppressWarnings("unused")
        TurnHandler turnHandler = new TurnHandler(alchGame);

        new GamePresenter(alchGame, experimentHandler, new GameView()).start();
    }

    private static Board buildBoard(List<Ingredient> ingredients) {
        Map<String, Slot> slots = new HashMap<>();
        for (GameConfig.SlotSpec spec : GameConfig.SLOTS) {
            slots.put(spec.id(), new Slot(spec.id(), new Resources(spec.ingredientCount(), spec.favorCount())));
        }
        OrderSpace orderSpace = new OrderSpace(slots);

        List<Ingredient> ingredientDeckList = new ArrayList<>(ingredients);
        Collections.shuffle(ingredientDeckList);
        Deque<Ingredient> ingredientDeck = new ArrayDeque<>(ingredientDeckList);

        Deque<Favor> favorDeck = new ArrayDeque<>();
        for (int i = 0; i < GameConfig.FAVOR_DECK_SIZE; i++)
            favorDeck.add(new Favor("favor-" + i));

        return new Board(new HashMap<>(), orderSpace, ingredientDeck, favorDeck);
    }
}
