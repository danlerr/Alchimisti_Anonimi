package alchgame.presentation;

import alchgame.application.observer.GameStateDTO;
import alchgame.model.player.Player;

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
        Player player = state.currentPlayer();
        String actionId = state.currentActionId();

        view.showResolutionStep(actionId, player.getName());
        dispatcher.dispatch(actionId, player);
        view.showPlayerStatus(player.getGold(), player.getReputation(), player.getActionCubes());
        view.showIngredients(player.getIngredientsFromLab().stream()
                .map(i -> i.getName()).toList());
    }
}
