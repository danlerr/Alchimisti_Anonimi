package alchgame.view.phase;

import alchgame.controller.StartGameController;
import alchgame.view.GameView;

public class SetupView {

    private final GameView view;

    public SetupView(GameView view) {
        this.view = view;
    }

    public void run(StartGameController ctrl) {
        view.printSection("NUOVA PARTITA");
        int count = view.askPlayerCount(ctrl.getMinPlayers(), ctrl.getMaxPlayers());
        ctrl.setPlayerNumber(count);
        for (int i = 1; i <= count; i++) {
            try {
                ctrl.setPlayerName(view.askPlayerName(i));
            } catch (IllegalArgumentException e) {
                view.showError(e.getMessage());
                i--;
            }
        }
        ctrl.startGame();
    }
}
