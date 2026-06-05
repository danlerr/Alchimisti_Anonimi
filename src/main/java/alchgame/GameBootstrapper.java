package alchgame;

import alchgame.application.*;
import alchgame.config.GameConfig;
import alchgame.model.alchemy.*;
import alchgame.model.alchemy.effect.PotionEffectRegistry;
import alchgame.model.board.Board;
import alchgame.model.factory.AlchemyFactory;
import alchgame.model.factory.BoardFactory;
import alchgame.model.factory.PlayerFactory;
import alchgame.model.game.*;
import alchgame.presentation.*;
import alchgame.presentation.DeclarationPresenter;
import alchgame.presentation.ResolutionPresenter;
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
        AlchGame alchGame = createGame(board, alchemicMapping);

        PlayerFactory playerFactory = new PlayerFactory(ingredients, formulas);
        StartGameService startGameService = createStartGameService(alchGame, playerFactory);

        StartGameController startGameController = new StartGameController(startGameService);
        OrderController orderController = new OrderController(alchGame::getCurrentRound, board);
        DeclarationController declarationController = new DeclarationController(alchGame::getCurrentRound, board);
        ForageController forageController = new ForageController(alchGame::getCurrentRound, GameConfig.FORAGE_YIELD);
        TransmuteController transmuteController = new TransmuteController(alchGame::getCurrentRound, GameConfig.TRASMUTE_GOLD);
        ExperimentController experimentController = new ExperimentController(alchGame, new AlchemicAlgorithm(alchemicMapping), registry);

        GameController gameController = new GameController(alchGame);

        orderController.attach(gameController);
        declarationController.attach(gameController);
        forageController.attach(gameController);
        transmuteController.attach(gameController);
        experimentController.attach(gameController);

        // --- Presentation layer ---
        GameView view = new GameView();

        SetupPresenter setupPresenter = new SetupPresenter(startGameController, view);

        ForagePresenter foragePresenter       = new ForagePresenter(view, forageController);
        TransmutePresenter transmutePresenter = new TransmutePresenter(view, transmuteController);
        ExperimentPresenter experimentPresenter = new ExperimentPresenter(view, experimentController);

        ActionDispatcher dispatcher = new ActionDispatcher(Map.of(
                GameConfig.AS_FORAGE,     () -> foragePresenter.run(),
                GameConfig.AS_TRANSMUTE,  () -> transmutePresenter.run(),
                GameConfig.AS_EXPERIMENT, () -> experimentPresenter.run()
        ));

        OrderPresenter orderPhasePresenter = new OrderPresenter(orderController, gameController, view);
        DeclarationPresenter declarationPhasePresenter = new DeclarationPresenter(declarationController, gameController, view);
        ResolutionPresenter resolutionPhasePresenter = new ResolutionPresenter(
                dispatcher, gameController, view);

        GamePresenter gamePresenter = new GamePresenter(
                gameController, view,
                setupPresenter,
                orderPhasePresenter,
                declarationPhasePresenter,
                resolutionPhasePresenter);

        gamePresenter.run();
    }

    private static AlchGame createGame(Board board, AlchemicMapping alchemicMapping) {
        return new AlchGame(
                board,
                alchemicMapping,
                GameConfig.STARTING_ACTION_CUBES,
                GameConfig.STARTING_GOLD,
                GameConfig.STARTING_REPUTATION,
                GameConfig.STARTING_INGREDIENTS,
                GameConfig.TOTAL_ROUNDS,
                GameConfig.MIN_PLAYERS,
                GameConfig.MAX_PLAYERS,

                Map.of(GameConfig.TARGET_STUDENT_ID, new Student(GameConfig.STUDENT_UNHAPPY_COST)),
                GameConfig.SELF_ID
        );
    }

    private static StartGameService createStartGameService(AlchGame alchGame, PlayerFactory playerFactory) {
        return new StartGameService(
                alchGame,
                playerFactory,
                new Random()
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
