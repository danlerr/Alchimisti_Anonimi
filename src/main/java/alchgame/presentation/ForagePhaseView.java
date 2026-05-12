package alchgame.presentation;

import alchgame.controller.ForageController;

public class ForagePhaseView {

    private final GameView view;
    private final ForageController forageController;

    public ForagePhaseView(GameView view, ForageController forageController) {
        this.view = view;
        this.forageController = forageController;
    }

    public void run() {
        // TODO: implementare il flusso di ricerca ingrediente
    }
}
