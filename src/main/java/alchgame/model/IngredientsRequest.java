package alchgame.model;

import java.util.List;
public class IngredientsRequest {

    private final List<Ingredient> ingredients;

    public IngredientsRequest(List<Ingredient> ingredients) {
        this.ingredients = List.copyOf(ingredients);
    }

    public List<Ingredient> getIngredients() { return ingredients; }
}
