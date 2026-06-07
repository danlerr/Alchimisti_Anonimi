package alchgame.presentation;

import alchgame.application.ForageController;

public class ForagePresenter {

    private final GameView view;
    private final ForageController forageController;

    public ForagePresenter(GameView view, ForageController forageController) {
        this.view = view;
        this.forageController = forageController;
    }

    public void run() {
        try {
            forageController.forageIngredient();
            view.showForageResult();
        } catch (IllegalStateException e) {
            view.showInvalidInput(e.getMessage());
        }
        view.promptContinue();
        forageController.endForage();
    }
}
