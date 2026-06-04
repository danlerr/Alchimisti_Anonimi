package alchgame.presentation;

import alchgame.application.TransmuteController;
import alchgame.model.alchemy.Ingredient;

import java.util.List;

public class TransmuteActionPresenter {

    private final GameView view;
    private final TransmuteController transmuteController;

    public TransmuteActionPresenter(GameView view, TransmuteController transmuteController) {
        this.view = view;
        this.transmuteController = transmuteController;
    }

    public void run() {
        List<Ingredient> ingredients = transmuteController.getIngredients();

        if (ingredients.isEmpty()) {
            view.showInvalidInput("Non hai ingredienti da tramutare.");
            return;
        }

        view.showIngredients(ingredients.stream().map(Ingredient::getName).toList());

        int choice = view.promptIngredientChoice("  Scegli ingrediente da tramutare", ingredients.size());

        try {
            String ingredientId = ingredients.get(choice - 1).getId();
            int updatedGold = transmuteController.transmuteIngredient(ingredientId);
            view.showTransmutationResult(updatedGold);
        } catch (IllegalArgumentException e) {
            view.showInvalidInput(e.getMessage());
        }
    }
}
