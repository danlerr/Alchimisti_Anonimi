package alchgame.presentation;

import alchgame.controller.GameFlowController;

public class GamePresenter {

    private final GameFlowController gameFlow;
    private final GameView view;
    private final SetupPresenter setupPresenter;
    private final OrderPhasePresenter orderPhasePresenter;
    private final DeclarationPhasePresenter declarationPhasePresenter;
    private final ResolutionPhasePresenter resolutionPhasePresenter;

    public GamePresenter(
            GameFlowController gameFlow,
            GameView view,
            SetupPresenter setupPresenter,
            OrderPhasePresenter orderPhasePresenter,
            DeclarationPhasePresenter declarationPhasePresenter,
            ResolutionPhasePresenter resolutionPhasePresenter
    ) {
        this.gameFlow = gameFlow;
        this.view = view;
        this.setupPresenter = setupPresenter;
        this.orderPhasePresenter = orderPhasePresenter;
        this.declarationPhasePresenter = declarationPhasePresenter;
        this.resolutionPhasePresenter = resolutionPhasePresenter;
    }

    public void run() {
        setupPresenter.run();

        while (true) {
            view.showRoundStart(gameFlow.getCurrentRoundNumber(), gameFlow.getTotalRounds());

            orderPhasePresenter.run();
            gameFlow.advancePhase();

            declarationPhasePresenter.run();
            gameFlow.advancePhase();

            resolutionPhasePresenter.run();

            view.showRoundEnd(gameFlow.getCurrentRoundNumber());

            if (gameFlow.isGameOver()) break;
            gameFlow.advanceRound();
        }

        view.showGameOver(gameFlow.getPlayers());
    }
}
