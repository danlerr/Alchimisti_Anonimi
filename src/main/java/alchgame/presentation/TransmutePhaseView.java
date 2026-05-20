package alchgame.presentation;

import alchgame.controller.TransmuteController;
import alchgame.model.alchemy.Ingredient;

import java.util.List;

public class TransmutePhaseView {

    private final GameView view;
    private final TransmuteController transmuteController;

    public TransmutePhaseView(GameView view, TransmuteController transmuteController) {
        this.view = view;
        this.transmuteController = transmuteController;
    }

    public void run() {
        List<Ingredient> ingredients = transmuteController.getLabIngredients();

        if (ingredients.isEmpty()) {
            view.showInvalidInput("Non hai ingredienti da tramutare.");
            return;
        }

        view.showIngredients(ingredients);

        int choice = view.promptIngredientChoice(
                "  Scegli ingrediente da tramutare",
                ingredients.size()
        );

        try {
            int updatedGold = transmuteController.transmuteIngredient(choice - 1);
            view.showTransmutationResult(updatedGold);
        } catch (IllegalArgumentException e) {
            view.showInvalidInput(e.getMessage());
        }
    }
}
