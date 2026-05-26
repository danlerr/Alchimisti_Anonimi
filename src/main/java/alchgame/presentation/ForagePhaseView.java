package alchgame.presentation;

import alchgame.controller.ForageController;
import alchgame.model.alchemy.Ingredient;

import java.util.List;

public class ForagePhaseView {

    private final GameView view;
    private final ForageController forageController;

    public ForagePhaseView(GameView view, ForageController forageController) {
        this.view = view;
        this.forageController = forageController;
    }

    public void run() {
        try {
            forageController.forageIngredient();
            view.showForageResult();
            List<Ingredient> ingredients = forageController.getLabIngredients();
            view.showIngredients(ingredients);
        } catch (IllegalStateException e) {
            view.showInvalidInput(e.getMessage());
        }
    }
}
