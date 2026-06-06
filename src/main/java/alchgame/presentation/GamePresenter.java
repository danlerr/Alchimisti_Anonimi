package alchgame.presentation;

import alchgame.application.GameController;
import alchgame.application.dto.GameStateDTO;
import alchgame.application.observer.GameObserver;

import java.util.ArrayDeque;
import java.util.Queue;

public class GamePresenter implements GameObserver {

    private final GameController gameController;
    private final GameView view;
    private final SetupPresenter setupPresenter;
    private final OrderPresenter orderPhasePresenter;
    private final DeclarationPresenter declarationPhasePresenter;
    private final ResolutionPresenter resolutionPhasePresenter;

    private final Queue<GameStateDTO> eventQueue = new ArrayDeque<>();

    public GamePresenter(GameController gameController,
                         GameView view,
                         SetupPresenter setupPresenter,
                         OrderPresenter orderPhasePresenter,
                         DeclarationPresenter declarationPhasePresenter,
                         ResolutionPresenter resolutionPhasePresenter) {
        this.gameController = gameController;
        this.view = view;
        this.setupPresenter = setupPresenter;
        this.orderPhasePresenter = orderPhasePresenter;
        this.declarationPhasePresenter = declarationPhasePresenter;
        this.resolutionPhasePresenter = resolutionPhasePresenter;
    }

    public void run() {
        gameController.attach(this);
        setupPresenter.run();

        view.showRoundStart(1, gameController.getTotalRounds());
        eventQueue.add(gameController.getInitialState());

        while (!eventQueue.isEmpty()) {
            dispatch(eventQueue.poll());
        }
    }

    @Override
    public void onGameEvent(GameStateDTO state) {
        eventQueue.add(state);
    }

    private void dispatch(GameStateDTO state) {
        switch (state.type()) {
            case GAME_OVER -> view.showGameOver(state.finalRanking());
            case ROUND_ENDED -> {
                view.showRoundEnd(state.roundNumber());
                view.showRoundStart(state.roundNumber() + 1, gameController.getTotalRounds());
                eventQueue.add(gameController.getInitialState());
            }
            case PHASE_CHANGED -> handlePhaseChanged(state);
            case TURN_ADVANCED -> handleTurn(state);
        }
    }

    private void handlePhaseChanged(GameStateDTO state) {
        switch (state.phaseType()) {
            case ORDER -> {
                orderPhasePresenter.showPhaseStart();
                orderPhasePresenter.handleTurn(state);
            }
            case DECLARATION -> {
                orderPhasePresenter.showPhaseEnd(state);
                declarationPhasePresenter.showPhaseStart();
                declarationPhasePresenter.handleTurn(state);
            }
            case RESOLUTION -> {
                resolutionPhasePresenter.showPhaseStart();
                resolutionPhasePresenter.handleTurn(state);
            }
        }
    }

    private void handleTurn(GameStateDTO state) {
        switch (state.phaseType()) {
            case ORDER      -> orderPhasePresenter.handleTurn(state);
            case DECLARATION -> declarationPhasePresenter.handleTurn(state);
            case RESOLUTION  -> resolutionPhasePresenter.handleTurn(state);
        }
    }
}
