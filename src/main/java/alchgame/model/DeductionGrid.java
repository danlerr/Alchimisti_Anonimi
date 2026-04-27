package alchgame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta la griglia di deduzione del giocatore: una tabella 8×8 dove
 * le righe sono i possibili alchemici del gioco e le colonne sono gli ingredienti.
 *
 * Una cella marcata (true) indica che il giocatore ha escluso quell'alchemico
 * come formula possibile per quell'ingrediente, analogamente a segnare una X
 * nella griglia fisica.
 */
public class DeductionGrid {

    /** Gli 8 alchemici del gioco: definiscono le righe della griglia. */
    private final List<AlchemicFormula> alchemics;

    /** Gli ingredienti del gioco: definiscono le colonne della griglia. */
    private final List<Ingredient> ingredients;

    /**
     * excluded[alcIdx][ingIdx] == true indica che l'alchemico alchemics.get(alcIdx)
     * è escluso per l'ingrediente ingredients.get(ingIdx).
     */
    private final boolean[][] excluded;

    public DeductionGrid(List<Ingredient> ingredients, List<AlchemicFormula> alchemics) {
        this.ingredients = List.copyOf(ingredients);
        this.alchemics   = List.copyOf(alchemics);
        this.excluded    = new boolean[alchemics.size()][ingredients.size()];
    }

    /**
     * Marca con una X la cella (ingrediente, alchemico): l'alchemico viene escluso
     * come possibile formula per quell'ingrediente.
     */
    public void exclude(Ingredient ingredient, AlchemicFormula alchemic) {
        int ingIdx = ingredients.indexOf(ingredient);
        int alcIdx = alchemics.indexOf(alchemic);
        if (ingIdx < 0) throw new IllegalArgumentException("Ingrediente non presente nella griglia: " + ingredient);
        if (alcIdx < 0) throw new IllegalArgumentException("Alchemico non presente nella griglia.");
        if (excluded[alcIdx][ingIdx]) {
            throw new IllegalArgumentException(
                "Questo alchemico è già escluso per " + ingredient.getName() + "."
            );
        }
        excluded[alcIdx][ingIdx] = true;
    }

    /** Restituisce true se la cella è marcata con X (alchemico escluso per quell'ingrediente). */
    public boolean isExcluded(Ingredient ingredient, AlchemicFormula alchemic) {
        int ingIdx = ingredients.indexOf(ingredient);
        int alcIdx = alchemics.indexOf(alchemic);
        if (ingIdx < 0 || alcIdx < 0) return false;
        return excluded[alcIdx][ingIdx];
    }

    /** Restituisce gli alchemici ancora possibili (non esclusi) per l'ingrediente dato. */
    public List<AlchemicFormula> getPossibleAlchemics(Ingredient ingredient) {
        int ingIdx = ingredients.indexOf(ingredient);
        if (ingIdx < 0) throw new IllegalArgumentException("Ingrediente non presente nella griglia.");
        List<AlchemicFormula> possible = new ArrayList<>();
        for (int a = 0; a < alchemics.size(); a++)
            if (!excluded[a][ingIdx])
                possible.add(alchemics.get(a));
        return possible;
    }

    public List<Ingredient> getIngredients() { return ingredients; }

    public List<AlchemicFormula> getAlchemics() { return alchemics; }

    /** Ritorna un riepilogo testuale delle esclusioni per ogni ingrediente. */
    public List<String> getExclusionsSummary() {
        List<String> result = new ArrayList<>();
        for (int ingIdx = 0; ingIdx < ingredients.size(); ingIdx++) {
            List<String> excl = new ArrayList<>();
            for (int alcIdx = 0; alcIdx < alchemics.size(); alcIdx++) {
                if (excluded[alcIdx][ingIdx]) excl.add("[" + (alcIdx + 1) + "]");
            }
            if (!excl.isEmpty())
                result.add(ingredients.get(ingIdx).getName() + ": esclusi " + String.join(", ", excl));
        }
        return result;
    }
}
