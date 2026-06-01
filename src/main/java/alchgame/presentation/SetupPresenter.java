package alchgame.presentation;

import alchgame.application.StartGameController;

public class SetupPresenter {

    private final StartGameController startController;
    private final GameView view;

    public SetupPresenter(StartGameController startController, GameView view) {
        this.startController = startController;
        this.view = view;
    }

    public void run() {
        view.showWelcome();

        while (true) {
            view.promptPlayerCount(startController.getMinPlayers(), startController.getMaxPlayers());
            int n = view.readInt();
            try {
                startController.setPlayerNumber(n);
                break;
            } catch (IllegalArgumentException e) {
                view.showInvalidInput(e.getMessage());
            }
        }

        while (startController.needsMorePlayerNames()) {
            view.promptPlayerName(startController.getInsertedPlayersCount() + 1);
            while (true) {
                String name = view.readLine();
                try {
                    startController.setPlayerName(name);
                    break;
                } catch (IllegalArgumentException e) {
                    view.showInvalidInput(e.getMessage());
                    view.promptPlayerName(startController.getInsertedPlayersCount() + 1);
                }
            }
        }

        startController.startGame();
    }
}
