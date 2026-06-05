package alchgame.presentation;

import alchgame.application.TransmuteController;
import alchgame.application.dto.IngredientDTO;

import java.util.List;

public class TransmuteActionPresenter {

    private final GameView view;
    private final TransmuteController transmuteController;

    public TransmuteActionPresenter(GameView view, TransmuteController transmuteController) {
        this.view = view;
        this.transmuteController = transmuteController;
    }

    public void run() {
        List<IngredientDTO> ingredients = transmuteController.getPlayerIngredients();

        if (ingredients.isEmpty()) {
            view.showInvalidInput("Non hai ingredienti da tramutare.");
            return;
        }

        view.showIngredients(ingredients.stream().map(IngredientDTO::name).toList());

        int choice = view.promptIngredientChoice("  Scegli ingrediente da tramutare", ingredients.size());

        try {
            String ingredientId = ingredients.get(choice - 1).id();
            int updatedGold = transmuteController.transmuteIngredient(ingredientId);
            view.showTransmutationResult(updatedGold);
        } catch (IllegalArgumentException e) {
            view.showInvalidInput(e.getMessage());
        }
    }
}
