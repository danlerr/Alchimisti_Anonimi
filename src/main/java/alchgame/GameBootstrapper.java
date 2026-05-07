package alchgame;

import alchgame.controller.*;
import alchgame.model.alchemy.*;
import alchgame.model.board.Board;
import alchgame.model.game.*;
import alchgame.resources.GameConfig;
import alchgame.service.*;

import java.util.List;
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
        AlchemyFactory alchemyFactory = new AlchemyFactory(GameConfig.PROPERTIES);
        List<Ingredient> ingredients = alchemyFactory.createIngredients();
        List<AlchemicFormula> formulas = alchemyFactory.createFormulas();

        AlchemicMapping alchemicMapping = alchemyFactory.createRandomMapping(ingredients, formulas);
        Student student = new Student();

        Board board = createBoardFactory().createBoard(ingredients);
        AlchGame alchGame = createGame(board);

        PlayerFactory playerFactory = new PlayerFactory(ingredients, formulas);
        StartGameService startGameService = createStartGameService(alchGame, playerFactory);

        // GamePresenter presenter = createPresenter(
        //         alchGame,
        //         startGameService,
        //         student,
        //         alchemicMapping
        // );

        //presenter.run();
    }

    private static AlchGame createGame(Board board) {
        return new AlchGame(
                board,
                GameConfig.STARTING_ACTION_CUBES,
                GameConfig.TOTAL_ROUNDS
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

    private static BoardFactory createBoardFactory() {
        return new BoardFactory(
                GameConfig.PROPERTIES,
                GameConfig.ACTION_ORDER,
                GameConfig.INGREDIENT_DECK_COPIES,
                GameConfig.FAVOR_DECK_SIZE
        );
    }

    // private static GamePresenter createPresenter(
    //         AlchGame alchGame,
    //         GameSetupService gameSetupService,
    //         Student student,
    //         AlchemicMapping alchemicMapping
    // ) {
    //     StartGameController startController = new StartGameController(gameSetupService);

    //     RoundController roundController = new RoundController(
    //             alchGame::getCurrentRound,
    //             GameConfig.ACTION_ORDER
    //     );

    //     ExperimentController experimentController = new ExperimentController(
    //             alchGame::getCurrentRound,
    //             student,
    //             new AlchemicAlgorithm(alchemicMapping),
    //             GameConfig.SELF_ID,
    //             GameConfig.TARGET_STUDENT_ID
    //     );

    //     GameFlowController gameFlowController = new GameFlowController(alchGame);

    //     GameView view = new GameView();
    //     ExperimentPhaseView experimentPhaseView = new ExperimentPhaseView(view, experimentController);

    //     return new GamePresenter(
    //             gameFlowController,
    //             startController,
    //             roundController,
    //             experimentPhaseView,
    //             view
    //     );
    // }
}
