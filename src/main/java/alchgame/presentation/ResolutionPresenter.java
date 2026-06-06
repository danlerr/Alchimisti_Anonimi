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
        PlayerDTO player = state.currentPlayer();
        view.showResolutionStep(state.currentActionId(), player.name());
        dispatcher.dispatch(state.currentActionId());
    }
}
