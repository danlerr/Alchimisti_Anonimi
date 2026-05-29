package alchgame.presentation;

import alchgame.model.player.Player;
import alchgame.service.GameSession;

public class ResolutionPhasePresenter {

    private final GameSession gameSession;
    private final ActionDispatcher dispatcher;
    private final GameView view;

    public ResolutionPhasePresenter(GameSession gameSession, ActionDispatcher dispatcher, GameView view) {
        this.gameSession = gameSession;
        this.dispatcher = dispatcher;
        this.view = view;
    }

    public void run() {
        view.showPhaseHeader("RISOLUZIONE");

        int total = gameSession.totalResolutionSteps();
        while (!gameSession.isResolutionComplete()) {
            String actionId = gameSession.currentResolutionActionId();
            Player player = gameSession.currentResolutionPlayer();
            view.showResolutionStep(gameSession.currentResolutionStepIndex() + 1, total, actionId, player.getName());
            dispatcher.dispatch(actionId);
            view.showPlayerStatus(player.getGold(), player.getReputation(), player.getActionCubes());
            view.showIngredients(player.getIngredientsFromLab());
            gameSession.advanceResolution();
        }
    }
}
