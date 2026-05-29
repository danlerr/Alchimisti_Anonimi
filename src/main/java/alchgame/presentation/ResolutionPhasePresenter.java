package alchgame.presentation;

import alchgame.controller.ResolutionCoordinator;
import alchgame.model.player.Player;

public class ResolutionPhasePresenter {

    private final ResolutionCoordinator resolution;
    private final ActionDispatcher dispatcher;
    private final GameView view;

    public ResolutionPhasePresenter(ResolutionCoordinator resolution, ActionDispatcher dispatcher, GameView view) {
        this.resolution = resolution;
        this.dispatcher = dispatcher;
        this.view = view;
    }

    public void run() {
        view.showPhaseHeader("RISOLUZIONE");

        int total = resolution.totalSteps();
        while (!resolution.isComplete()) {
            String actionId = resolution.currentActionId();
            Player player = resolution.currentPlayer();
            resolution.setCurrentPlayer(player);
            view.showResolutionStep(resolution.currentStepIndex() + 1, total,
                    actionId, player.getName());
            dispatcher.dispatch(actionId);
            view.showPlayerStatus(player.getGold(),
                    player.getReputation(), player.getActionCubes());
            view.showIngredients(player.getIngredientsFromLab());
            resolution.advance();
        }
    }
}
