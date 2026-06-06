package alchgame.presentation;

import alchgame.application.dto.PlayerDTO;
import alchgame.application.dto.GameStateDTO;

public class ResolutionPresenter {

    private final ActionDispatcher dispatcher;
    private final GameView view;

    public ResolutionPresenter(ActionDispatcher dispatcher,
                                    GameView view) {
        this.dispatcher = dispatcher;
        this.view = view;
    }

    public void showPhaseStart() {
        view.showPhaseHeader("RISOLUZIONE");
    }

    public void handleTurn(GameStateDTO state) {
        view.clearScreen();
        PlayerDTO player = state.currentPlayer();
        view.showResolutionStep(state.currentActionId(), player.name(),
                player.gold(), player.reputation(), player.actionCubes());
        dispatcher.dispatch(state.currentActionId());
    }
}
