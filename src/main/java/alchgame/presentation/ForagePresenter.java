package alchgame.presentation;

import alchgame.application.ForageController;
import alchgame.application.dto.IngredientDTO;

import java.util.List;

public class ForagePresenter {

    private final GameView view;
    private final ForageController forageController;

    public ForagePresenter(GameView view, ForageController forageController) {
        this.view = view;
        this.forageController = forageController;
    }

    public void run() {
        try {
            List<IngredientDTO> received = forageController.forageIngredient();
            view.showForageResult(received);
        } catch (IllegalStateException e) {
            view.showInvalidInput(e.getMessage());
        }
        view.promptContinue();
        forageController.endForage();
    }
}
