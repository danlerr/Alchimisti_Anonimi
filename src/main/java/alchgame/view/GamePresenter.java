package alchgame.view;

import alchgame.controller.GameFlowController;
import alchgame.controller.StartGameController;
import alchgame.controller.TurnController;
import alchgame.view.phase.DeclarationPhaseView;
import alchgame.view.phase.ExperimentPhaseView;
import alchgame.view.phase.OrderPhaseView;
import alchgame.view.phase.ResolutionPhaseView;
import alchgame.view.phase.SetupView;

public class GamePresenter {

    private final GameFlowController    gameFlowController;
    private final StartGameController   startController;
    private final TurnController        turnController;
    private final SetupView             setupView;
    private final OrderPhaseView        orderPhaseView;
    private final DeclarationPhaseView  declarationPhaseView;
    private final ResolutionPhaseView   resolutionPhaseView;
    private final GameView              view;

    public GamePresenter(GameFlowController gameFlowController,
                         StartGameController startController,
                         TurnController turnController,
                         ExperimentPhaseView experimentPhaseView,
                         GameView view) {
        this.gameFlowController   = gameFlowController;
        this.startController      = startController;
        this.turnController       = turnController;
        this.view                 = view;
        this.setupView            = new SetupView(view);
        this.orderPhaseView       = new OrderPhaseView(view);
        this.declarationPhaseView = new DeclarationPhaseView(view);
        this.resolutionPhaseView  = new ResolutionPhaseView(view, turnController, experimentPhaseView, gameFlowController);
    }

    public void run() {
        view.clearScreen();
        view.printHeader();
        setupView.run(startController);
        while (!gameFlowController.isGameOver()) {
            view.showRoundStart(gameFlowController.getCurrentRound(), gameFlowController.getTotalRounds());
            orderPhaseView.run(turnController);
            declarationPhaseView.run(turnController);
            resolutionPhaseView.run();
            view.showRoundEnd(gameFlowController.getCurrentRound());
            gameFlowController.endRound();
        }
        view.showGoodbye();
    }
}
