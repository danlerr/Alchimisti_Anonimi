package alchgame.presentation;

import alchgame.application.StartGameController;
import alchgame.model.game.AlchGame;

public class SetupPresenter {

    private final StartGameController startController;
    private final AlchGame alchGame;
    private final GameView view;

    public SetupPresenter(StartGameController startController, AlchGame alchGame, GameView view) {
        this.startController = startController;
        this.alchGame = alchGame;
        this.view = view;
    }

    public void run() {
        view.showWelcome();

        while (true) {
            view.promptPlayerCount(alchGame.getMinPlayers(), alchGame.getMaxPlayers());
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
