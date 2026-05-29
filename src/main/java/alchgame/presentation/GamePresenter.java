package alchgame.presentation;

import alchgame.service.GameSession;

public class GamePresenter {

    private final GameSession gameSession;
    private final GameView view;
    private final SetupPresenter setupPresenter;
    private final OrderPhasePresenter orderPhasePresenter;
    private final DeclarationPhasePresenter declarationPhasePresenter;
    private final ResolutionPhasePresenter resolutionPhasePresenter;

    public GamePresenter(
            GameSession gameSession,
            GameView view,
            SetupPresenter setupPresenter,
            OrderPhasePresenter orderPhasePresenter,
            DeclarationPhasePresenter declarationPhasePresenter,
            ResolutionPhasePresenter resolutionPhasePresenter
    ) {
        this.gameSession = gameSession;
        this.view = view;
        this.setupPresenter = setupPresenter;
        this.orderPhasePresenter = orderPhasePresenter;
        this.declarationPhasePresenter = declarationPhasePresenter;
        this.resolutionPhasePresenter = resolutionPhasePresenter;
    }

    public void run() {
        setupPresenter.run();

        while (true) {
            view.showRoundStart(gameSession.getCurrentRoundNumber(), gameSession.getTotalRounds());

            orderPhasePresenter.run();
            gameSession.tryAdvancePhase();

            declarationPhasePresenter.run();
            gameSession.tryAdvancePhase();

            resolutionPhasePresenter.run();

            view.showRoundEnd(gameSession.getCurrentRoundNumber());

            if (gameSession.isGameOver()) break;
            gameSession.advanceRound();
        }

        view.showGameOver(gameSession.getPlayers());
    }
}
