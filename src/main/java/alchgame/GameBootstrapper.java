package alchgame;

import alchgame.controller.*;
import alchgame.model.alchemy.*;
import alchgame.model.board.Board;
import alchgame.model.game.*;
import alchgame.resources.GameConfig;
import alchgame.service.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * Composition root dell'applicazione.
 *
 * Si occupa di creare e collegare gli oggetti principali necessari
 * all'avvio del gioco: model, service, controller e view.
 */
class GameBootstrapper {

    static void run() {
        List<Ingredient> ingredients = createIngredients();
        List<AlchemicFormula> formulas = GameConfig.FORMULAS;

        AlchemicMapping alchemicMapping = createRandomMapping(ingredients, formulas);
        Student student = new Student();

        Board board = new BoardFactory().createBoard(ingredients);
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

    private static List<Ingredient> createIngredients() {
        return GameConfig.INGREDIENT_NAMES.stream()
                .map(Ingredient::new)
                .toList();
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

    // private static GamePresenter createPresenter(
    //         AlchGame alchGame,
    //         GameSetupService gameSetupService,
    //         Student student,
    //         AlchemicMapping alchemicMapping
    // ) {
    //     StartGameController startController = new StartGameController(gameSetupService);

    //     RoundController roundController = new RoundController(alchGame::getCurrentRound);

    //     ExperimentController experimentController = new ExperimentController(
    //             alchGame::getCurrentRound,
    //             student,
    //             new AlchemicAlgorithm(alchemicMapping)
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

    private static AlchemicMapping createRandomMapping(
            List<Ingredient> ingredients,
            List<AlchemicFormula> formulas
    ) {
        List<AlchemicFormula> shuffled = new ArrayList<>(formulas);
        Collections.shuffle(shuffled);

        Map<Ingredient, AlchemicFormula> rawMapping = new HashMap<>();

        for (int i = 0; i < ingredients.size(); i++) {
            rawMapping.put(ingredients.get(i), shuffled.get(i));
        }

        return new AlchemicMapping(rawMapping);
    }
}