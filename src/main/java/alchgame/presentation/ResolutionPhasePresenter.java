package alchgame.presentation;

import alchgame.controller.GameFlowController;
import alchgame.model.player.Player;

public class ResolutionPhasePresenter {

    private final GameFlowController gameFlow;
    private final ActionDispatcher dispatcher;
    private final GameView view;

    public ResolutionPhasePresenter(GameFlowController gameFlow, ActionDispatcher dispatcher, GameView view) {
        this.gameFlow = gameFlow;
        this.dispatcher = dispatcher;
        this.view = view;
    }

    public void run() {
        view.showPhaseHeader("RISOLUZIONE");

        int total = gameFlow.getResolutionTotalSteps();
        while (!gameFlow.isResolutionComplete()) {
            String actionId = gameFlow.currentResolutionActionId();
            Player player = gameFlow.currentResolutionPlayer();
            gameFlow.setCurrentPlayer(player);
            view.showResolutionStep(gameFlow.getResolutionStepIndex() + 1, total,
                    actionId, player.getName());
            dispatcher.dispatch(actionId);
            view.showPlayerStatus(player.getGold(),
                    player.getReputation(), player.getActionCubes());
            view.showIngredients(player.getIngredientsFromLab());
            gameFlow.markCurrentPlayerResolved();
        }
    }
}
