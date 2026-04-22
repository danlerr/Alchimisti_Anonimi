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

    /**
     * Aggiorna il lab dopo un esperimento:
     *   1. discard i due ingredienti usati
     *   2. registra l'osservazione nella DeductionGrid
     *   3. registra il risultato nel ResultsTriangle
     */
    public void updatePrivateLab(Ingredient i1, Ingredient i2, Potion potion) {
        ingredients.remove(i1);
        ingredients.remove(i2);
        resultsTriangle.recordResult(i1, i2, potion);
    }
}
