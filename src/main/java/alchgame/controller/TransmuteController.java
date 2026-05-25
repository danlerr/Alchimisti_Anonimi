package alchgame.controller;

import alchgame.model.alchemy.Ingredient;
import alchgame.model.game.Round;
import alchgame.model.player.Player;

import java.util.List;
import java.util.function.Supplier;

public class TransmuteController {

    private final Supplier<Round> round;

    public TransmuteController(Supplier<Round> round) {
        this.round = round;
    }

    public List<Ingredient> getLabIngredients() {
        return round.get().getCurrentPlayer().getIngredientsFromLab();
    }

    public int transmuteIngredient(String ingredientId) {
        Player player = round.get().getCurrentPlayer();
        Ingredient selected = player.findIngredientInLabById(ingredientId);
        player.removeIngredient(selected);
        player.addGold(1);
        return player.getGold();
    }
}
