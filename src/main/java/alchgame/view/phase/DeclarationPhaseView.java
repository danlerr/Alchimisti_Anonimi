package alchgame.view.phase;

import alchgame.controller.TurnController;
import alchgame.view.GameView;

public class DeclarationPhaseView {

    private final GameView view;

    public DeclarationPhaseView(GameView view) {
        this.view = view;
    }

    public void run(TurnController tc) {
        view.printSection("FASE DICHIARAZIONE AZIONI");
        for (String playerName : tc.getDeclarationPhaseOrder()) {
            tc.setCurrentPlayerByName(playerName);
            while (tc.getCurrentPlayerActionCubes() > 0) {
                String actionId = view.askActionDeclaration(
                        playerName, tc.getActionOptions(), tc.getCurrentPlayerActionCubes());
                if (actionId == null) break;
                try {
                    tc.declareAction(actionId);
                } catch (IllegalStateException e) {
                    view.showError(e.getMessage());
                }
            }
        }
    }
}
