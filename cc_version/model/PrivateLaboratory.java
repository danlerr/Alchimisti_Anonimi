package alchgame.model;

import java.util.ArrayList;
import java.util.List;

public class PrivateLaboratory {
    private final DeductionGrid deductionGrid;
    private final ResultsTriangle resultsTriangle;
    private final List<Ingredient> ingredients = new ArrayList<>();

    public PrivateLaboratory(DeductionGrid deductionGrid, ResultsTriangle resultsTriangle) {
        this.deductionGrid = deductionGrid;
        this.resultsTriangle = resultsTriangle;
    }

    public DeductionGrid getDeductionGrid() {
        return deductionGrid;
    }

    public ResultsTriangle getResultsTriangle() {
        return resultsTriangle;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public Ingredient getIngredientCard(int index) {
        if (index < 0 || index >= ingredients.size()) {
            throw new IllegalArgumentException("Ingredient index out of range: " + index);
        }
        return ingredients.get(index);
    }

    public void discardIngredients(Ingredient ingredient1, Ingredient ingredient2) {
        ingredients.remove(ingredient1);
        ingredients.remove(ingredient2);
    }

    public void updateLab(Ingredient ingredient1, Ingredient ingredient2, Potion potion) {
        discardIngredients(ingredient1, ingredient2);
        deductionGrid.addObservation(ingredient1, ingredient2, potion);
        resultsTriangle.recordResult(ingredient1, ingredient2, potion);
    }
}
