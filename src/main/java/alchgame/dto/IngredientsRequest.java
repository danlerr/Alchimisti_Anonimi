package alchgame.dto;

import java.util.List;

import alchgame.model.Ingredient;
public final class IngredientsRequest implements ExperimentStep {

    private final List<Ingredient> ingredients;

    public IngredientsRequest(List<Ingredient> ingredients) {
        this.ingredients = List.copyOf(ingredients);
    }

    public List<Ingredient> getIngredients() { return ingredients; }
}
