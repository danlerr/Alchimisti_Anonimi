package alchgame.presentation;

import alchgame.application.dto.PlayerDTO;
import alchgame.application.observer.GameStateDTO;

public class ResolutionPhasePresenter {

    private final ActionDispatcher dispatcher;
    private final GameView view;

    public ResolutionPhasePresenter(ActionDispatcher dispatcher, GameView view) {
        this.dispatcher = dispatcher;
        this.view = view;
    }

    public void showPhaseStart() {
        view.showPhaseHeader("RISOLUZIONE");
    }

    public void handleTurn(GameStateDTO state) {
        PlayerDTO player = state.currentPlayer();
        String actionId = state.currentActionId();

        view.showResolutionStep(actionId, player.name());
        dispatcher.dispatch(actionId);
        view.showPlayerStatus(player.gold(), player.reputation(), player.actionCubes());
        view.showIngredients(player.ingredients().stream()
                .map(i -> i.name()).toList());
    }
}
