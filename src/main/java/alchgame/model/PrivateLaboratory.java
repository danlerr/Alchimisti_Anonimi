package alchgame.model;

import java.util.ArrayList;
import java.util.List;

public class PrivateLaboratory {

    private final List<Ingredient> ingredients;
    private final DeductionGrid    deductionGrid;
    private final ResultsTriangle  resultsTriangle;

    public PrivateLaboratory(List<Ingredient> initialIngredients,
                             DeductionGrid deductionGrid,
                             ResultsTriangle resultsTriangle) {
        this.ingredients     = new ArrayList<>(initialIngredients);
        this.deductionGrid   = deductionGrid;
        this.resultsTriangle = resultsTriangle;
    }

    public List<Ingredient> getIngredients() { return List.copyOf(ingredients); }
    public DeductionGrid getDeductionGrid() { return deductionGrid;   }
    public ResultsTriangle getResultsTriangle() { return resultsTriangle; }

    public void addIngredient(Ingredient ingredient) { ingredients.add(ingredient); }

    public void removeIngredient(Ingredient ingredient) {
        if (!ingredients.remove(ingredient)) {
            throw new IllegalArgumentException("Ingrediente non presente nel laboratorio.");
        }
    }

    /**
     *   1. discard i due ingredienti usati
     *   2. registra l'osservazione nella DeductionGrid
     */
    public void applyExperimentResult(Ingredient i1, Ingredient i2, Potion potion) {
        removeIngredient(i1);
        removeIngredient(i2);
        resultsTriangle.recordResult(i1, i2, potion);
    }
}
