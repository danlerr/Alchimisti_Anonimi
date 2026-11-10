package alchgame;

import alchgame.application.*;
import alchgame.config.GameConfig;
import alchgame.model.alchemy.*;
import alchgame.model.alchemy.potionEffect.PotionEffectRegistry;
import alchgame.model.board.Board;
import alchgame.model.factory.AlchemyFactory;
import alchgame.model.factory.BoardFactory;
import alchgame.model.factory.PlayerFactory;
import alchgame.model.game.*;
import alchgame.presentation.*;

import java.util.List;
import java.util.Map;

/**
 * Composition root dell'applicazione.
 */
class GameBootstrapper {

    static void run() {
        //factory
        AlchemyFactory alchemyFactory = new AlchemyFactory();
        List<Ingredient> ingredients = alchemyFactory.createIngredients(GameConfig.getIngredientNames());
        List<AlchemicFormula> formulas = alchemyFactory.createFormulas(GameConfig.getFormulaSpecs());
        AlchemicMapping alchemicMapping = alchemyFactory.createRandomMapping(ingredients, formulas);
        PotionEffectRegistry registry = AlchemyFactory.createRegistry();

        Board board = createBoardFactory().createBoard(ingredients);
        PlayerFactory playerFactory = new PlayerFactory(ingredients, formulas);
        AlchGame alchGame = createGame(board, alchemicMapping, playerFactory);
        
        //controller
        StartGameController startGameController = new StartGameController(alchGame);
        OrderController orderController = new OrderController(alchGame::getCurrentRound);
        DeclarationController declarationController = new DeclarationController(alchGame::getCurrentRound);
        ForageController forageController = new ForageController(alchGame::getCurrentRound, GameConfig.getForageYield());
        TransmuteController transmuteController = new TransmuteController(alchGame::getCurrentRound, GameConfig.getTrasmuteGold());
        ExperimentController experimentController = new ExperimentController(alchGame, new AlchemicAlgorithm(alchemicMapping), registry);
        GameController gameController = new GameController(alchGame);
        
        //obs wiring       gameController <-> ucControllers
        orderController.attach(gameController);
        declarationController.attach(gameController);
        forageController.attach(gameController);
        transmuteController.attach(gameController);
        experimentController.attach(gameController);

        //Presentation layer 
        GameView view = new GameView();
        SetupPresenter setupPresenter = new SetupPresenter(startGameController, view);
        ForagePresenter foragePresenter       = new ForagePresenter(view, forageController);
        TransmutePresenter transmutePresenter = new TransmutePresenter(view, transmuteController);
        ExperimentPresenter experimentPresenter = new ExperimentPresenter(view, experimentController);
        OrderPresenter orderPhasePresenter = new OrderPresenter(orderController, view);
        DeclarationPresenter declarationPhasePresenter = new DeclarationPresenter(declarationController, view);
        
        ActionDispatcher dispatcher = new ActionDispatcher(Map.of(
                GameConfig.getActionForage(),     () -> foragePresenter.run(),
                GameConfig.getActionTransmute(),  () -> transmutePresenter.run(),
                GameConfig.getActionExperiment(), () -> experimentPresenter.run()
        ));

        ResolutionPresenter resolutionPhasePresenter = new ResolutionPresenter(dispatcher, view);
        GamePresenter gamePresenter = new GamePresenter(
                gameController, view,
                setupPresenter,
                orderPhasePresenter,
                declarationPhasePresenter,
                resolutionPhasePresenter);

        gamePresenter.run();
    }

    private static AlchGame createGame(Board board, AlchemicMapping alchemicMapping,
                                        PlayerFactory playerFactory) {
        return new AlchGame(
                board,
                alchemicMapping,
                playerFactory,
                GameConfig.getStartingActionCubes(),
                GameConfig.getStartingGold(),
                GameConfig.getStartingReputation(),
                GameConfig.getStartingIngredients(),
                GameConfig.getTotalRounds(),
                GameConfig.getMinPlayers(),
                GameConfig.getMaxPlayers(),

                Map.of(GameConfig.getTargetStudentId(), new Student(GameConfig.getStudentUnhappyCost())),
                GameConfig.getSelfId()
        );
    }


    private static BoardFactory createBoardFactory() {
        return new BoardFactory(
                GameConfig.getSlotSpecs(),
                GameConfig.getActionOrder(),
                GameConfig.getIngredientDeckCopies(),
                GameConfig.getFavorDeckSize(),
                GameConfig.getFavorErboristIngredients(),
                GameConfig.getFavorAssistantCubes()
        );
    }
}
