package alchgame;

import alchgame.controller.*;
import alchgame.model.alchemy.*;
import alchgame.model.alchemy.effect.PotionEffectRegistry;
import alchgame.model.board.Board;
import alchgame.model.game.*;
import alchgame.presentation.*;
import alchgame.resources.GameConfig;
import alchgame.service.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Composition root dell'applicazione.
 * Crea e collega tutti gli oggetti; non contiene logica di dominio.
 */
class GameBootstrapper {

    static void run() {
        AlchemyFactory alchemyFactory = new AlchemyFactory();
        List<Ingredient> ingredients = alchemyFactory.createIngredients(GameConfig.getIngredientNames());
        List<AlchemicFormula> formulas = alchemyFactory.createFormulas(GameConfig.getFormulaSpecs());
        AlchemicMapping alchemicMapping = alchemyFactory.createRandomMapping(ingredients, formulas);
        PotionEffectRegistry registry = AlchemyFactory.createRegistry();

        Board board = createBoardFactory().createBoard(ingredients);
        AlchGame alchGame = createGame(board);

        PlayerFactory playerFactory = new PlayerFactory(ingredients, formulas);
        StartGameService startGameService = createStartGameService(alchGame, playerFactory);
        GameSession gameSession = new GameSession(alchGame);

        GamePresenter presenter = createPresenter(alchGame, startGameService, gameSession, alchemicMapping, board, registry);
        presenter.run();
    }

    private static AlchGame createGame(Board board) {
        return new AlchGame(
                board,
                GameConfig.STARTING_ACTION_CUBES,
                GameConfig.TOTAL_ROUNDS,
                Map.of(GameConfig.TARGET_STUDENT_ID, new Student(GameConfig.STUDENT_UNHAPPY_COST)),
                GameConfig.SELF_ID
        );
    }

    private static StartGameService createStartGameService(AlchGame alchGame, PlayerFactory playerFactory) {
        return new StartGameService(
                alchGame,
                playerFactory,
                new Random(),
                GameConfig.MIN_PLAYERS,
                GameConfig.MAX_PLAYERS,
                GameConfig.STARTING_GOLD,
                GameConfig.STARTING_REPUTATION,
                GameConfig.STARTING_ACTION_CUBES,
                GameConfig.STARTING_INGREDIENTS
        );
    }

    private static GamePresenter createPresenter(
            AlchGame alchGame,
            StartGameService startGameService,
            GameSession gameSession,
            AlchemicMapping alchemicMapping,
            Board board,
            PotionEffectRegistry effectRegistry
    ) {
        // UC controller — azioni del giocatore
        StartGameController startController = new StartGameController(startGameService);
        OrderController orderController = new OrderController(alchGame, board);
        DeclarationController declarationController = new DeclarationController(alchGame, board);
        ExperimentController experimentController = new ExperimentController(alchGame, new AlchemicAlgorithm(alchemicMapping), effectRegistry);
        ForageController forageCtrl = new ForageController(alchGame::getCurrentRound, GameConfig.FORAGE_YIELD);
        TransmuteController transmuteCtrl = new TransmuteController(alchGame::getCurrentRound, GameConfig.TRASMUTE_GOLD);

        // Presenter layer
        GameView view = new GameView();
        ActionDispatcher dispatcher = new ActionDispatcher(Map.of(
                GameConfig.AS_EXPERIMENT, new ExperimentActionPresenter(view, experimentController)::run,
                GameConfig.AS_FORAGE, new ForageActionPresenter(view, forageCtrl)::run,
                GameConfig.AS_TRANSMUTE, new TransmuteActionPresenter(view, transmuteCtrl)::run
        ));

        return new GamePresenter(
                gameSession,
                view,
                new SetupPresenter(startController, view),
                new OrderPhasePresenter(orderController, view),
                new DeclarationPhasePresenter(declarationController, view),
                new ResolutionPhasePresenter(gameSession, dispatcher, view)
        );
    }

    private static BoardFactory createBoardFactory() {
        return new BoardFactory(
                GameConfig.getSlotSpecs(),
                GameConfig.ACTION_ORDER,
                GameConfig.INGREDIENT_DECK_COPIES,
                GameConfig.FAVOR_DECK_SIZE
        );
    }
}
