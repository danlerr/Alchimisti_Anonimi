package alchgame;

import alchgame.controller.*;
import alchgame.model.alchemy.*;
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
 *
 * Si occupa di creare e collegare gli oggetti principali necessari
 * all'avvio del gioco: configurazione, factory, model, service e controller.
 * Non contiene regole di dominio; orchestra solo il wiring iniziale delle
 * dipendenze.
 */
class GameBootstrapper {

    static void run() {
        AlchemyFactory alchemyFactory = new AlchemyFactory();
        List<Ingredient> ingredients = alchemyFactory.createIngredients(GameConfig.getIngredientNames());
        List<AlchemicFormula> formulas = alchemyFactory.createFormulas(GameConfig.getFormulaSpecs());

        AlchemicMapping alchemicMapping = alchemyFactory.createRandomMapping(ingredients, formulas);

        Board board = createBoardFactory().createBoard(ingredients);
        AlchGame alchGame = createGame(board);

        PlayerFactory playerFactory = new PlayerFactory(ingredients, formulas);
        StartGameService startGameService = createStartGameService(alchGame, playerFactory);

        GamePresenter presenter = createPresenter(
                alchGame,
                startGameService,
                alchemicMapping
        );

        presenter.run();
    }

    private static AlchGame createGame(Board board) {
        return new AlchGame(
                board,
                GameConfig.STARTING_ACTION_CUBES,
                GameConfig.TOTAL_ROUNDS,
                GameConfig.ACTION_ORDER,
                Map.of(GameConfig.TARGET_STUDENT_ID, new Student()),
                GameConfig.SELF_ID
        );
    }

    private static StartGameService createStartGameService(
            AlchGame alchGame,
            PlayerFactory playerFactory
    ) {
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
            AlchemicMapping alchemicMapping
    ) {
        StartGameController startController = new StartGameController(startGameService);

        OrderController orderController = new OrderController(alchGame);
        DeclarationController declarationController = new DeclarationController(alchGame);
        ResolutionCoordinator resolutionCoordinator = new ResolutionCoordinator(alchGame);

        ExperimentController experimentController = new ExperimentController(
                alchGame,
                new AlchemicAlgorithm(alchemicMapping)
        );

        ForageController    forageCtrl    = new ForageController(alchGame::getCurrentRound);
        TransmuteController transmuteCtrl = new TransmuteController(alchGame::getCurrentRound);

        GameFlowController gameFlowController = new GameFlowController(alchGame);
        GameView view = new GameView();
        ExperimentActionPresenter experimentActionPresenter = new ExperimentActionPresenter(view, experimentController);
        ForageActionPresenter     forageActionPresenter     = new ForageActionPresenter(view, forageCtrl);
        TransmuteActionPresenter  transmuteActionPresenter  = new TransmuteActionPresenter(view, transmuteCtrl);

        ActionDispatcher dispatcher = new ActionDispatcher(Map.of(
                GameConfig.AS_EXPERIMENT, experimentActionPresenter::run,
                GameConfig.AS_FORAGE,     forageActionPresenter::run,
                GameConfig.AS_TRANSMUTE,  transmuteActionPresenter::run
        ));

        SetupPresenter setupPresenter = new SetupPresenter(startController, view);
        OrderPhasePresenter orderPresenter = new OrderPhasePresenter(orderController, view);
        DeclarationPhasePresenter declarationPresenter = new DeclarationPhasePresenter(declarationController, view);
        ResolutionPhasePresenter resolutionPresenter = new ResolutionPhasePresenter(resolutionCoordinator, dispatcher, view);

        return new GamePresenter(
                gameFlowController,
                view,
                setupPresenter,
                orderPresenter,
                declarationPresenter,
                resolutionPresenter
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
