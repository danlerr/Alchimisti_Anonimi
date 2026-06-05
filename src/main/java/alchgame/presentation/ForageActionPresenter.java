package alchgame.presentation;

import alchgame.application.ForageController;
import alchgame.model.player.Player;

public class ForageActionPresenter {

    private final GameView view;
    private final ForageController forageController;

    public ForageActionPresenter(GameView view, ForageController forageController) {
        this.view = view;
        this.forageController = forageController;
    }

    public void run(Player player) {
        try {
            forageController.forageIngredient();
            view.showForageResult();
        } catch (IllegalStateException e) {
            view.showInvalidInput(e.getMessage());
        }
    }
}
