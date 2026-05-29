package alchgame.presentation;

import alchgame.model.player.Player;
import alchgame.service.GameFlowHandler;
import alchgame.service.GameSession;

import java.util.List;

public class GamePresenter implements GameFlowHandler {

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
        gameSession.run(this);
    }

    @Override
    public void onRoundStart(int current, int total) { view.showRoundStart(current, total); }

    @Override
    public void onOrderPhase()       { orderPhasePresenter.run(); }

    @Override
    public void onDeclarationPhase() { declarationPhasePresenter.run(); }

    @Override
    public void onResolutionPhase()  { resolutionPhasePresenter.run(); }

    @Override
    public void onRoundEnd(int current) { view.showRoundEnd(current); }

    @Override
    public void onGameOver(List<Player> players) { view.showGameOver(players); }
}
