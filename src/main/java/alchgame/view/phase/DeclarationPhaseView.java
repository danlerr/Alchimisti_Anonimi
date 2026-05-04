package alchgame.view.phase;

import alchgame.controller.TurnController;
import alchgame.dto.DeclarationPhaseState;
import alchgame.view.GameView;

public class DeclarationPhaseView {

    private final GameView view;

    public DeclarationPhaseView(GameView view) {
        this.view = view;
    }

    public void run(TurnController tc) {
        tc.advanceToDeclaration();
        view.printSection("FASE DICHIARAZIONE AZIONI");
        DeclarationPhaseState state = tc.getDeclarationPhaseState();
        for (String playerName : state.playerOrder()) {
            tc.setCurrentPlayerByName(playerName);
            while (tc.getCurrentPlayerActionCubes() > 0) {
                String actionId = view.askActionDeclaration(
                        playerName, state.actionOptions(), tc.getCurrentPlayerActionCubes());
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
