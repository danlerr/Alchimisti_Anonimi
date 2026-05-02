package alchgame.view.phase;

import alchgame.GameConfig;
import alchgame.controller.GameFlowController;
import alchgame.controller.TurnController;
import alchgame.view.GameView;

import java.util.List;

public class ResolutionPhaseView {

    private final GameView             view;
    private final TurnController       turnController;
    private final ExperimentPhaseView  experimentPhaseView;
    private final GameFlowController   gameFlowController;

    public ResolutionPhaseView(GameView view,
                               TurnController turnController,
                               ExperimentPhaseView experimentPhaseView,
                               GameFlowController gameFlowController) {
        this.view                = view;
        this.turnController      = turnController;
        this.experimentPhaseView = experimentPhaseView;
        this.gameFlowController  = gameFlowController;
    }

    public void run() {
        view.printSection("FASE RISOLUZIONE");
        for (String actionId : gameFlowController.getResolutionOrder()) {
            List<String> resolvers = turnController.getResolutionOrderFor(actionId);
            if (resolvers.isEmpty()) continue;
            view.showResolutionStart(actionId);
            for (String playerName : resolvers) {
                turnController.setCurrentPlayerByName(playerName);
                view.showResolvingPlayer(playerName, actionId);
                resolveAction(actionId);
            }
        }
    }

    private void resolveAction(String actionId) {
        switch (actionId) {
            case GameConfig.AS_EXPERIMENT -> experimentPhaseView.run();
            default -> view.pause("  [" + actionId + "] non ancora implementato. Premi INVIO...");
        }
    }
}
