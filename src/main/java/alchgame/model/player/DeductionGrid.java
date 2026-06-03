package alchgame.model.player;

import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.alchemy.Ingredient;

import java.util.List;
import java.util.Optional;

/**
 * Rappresenta la griglia di deduzione del giocatore: una tabella 8×8 dove
 * le righe sono i possibili alchemici del gioco e le colonne sono gli ingredienti.
 *
 * Una cella marcata (true) indica che il giocatore ha escluso quell'alchemico
 * come formula possibile per quell'ingrediente, analogamente a segnare una X
 * nella griglia fisica.
 */
public class DeductionGrid {

    private final List<AlchemicFormula> alchemics;
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

    public boolean isExcluded(Ingredient ingredient, AlchemicFormula alchemic) {
        int ingIdx = ingredients.indexOf(ingredient);
        int alcIdx = alchemics.indexOf(alchemic);
        if (ingIdx < 0 || alcIdx < 0) return false;
        return excluded[alcIdx][ingIdx];
    }
    /**
     * Controlla se per un dato ingrediente è rimasta esattamente una sola 
     * formula alchemica non esclusa. Se sì, la restituisce.
     */
    public Optional<AlchemicFormula> getDeducedFormula(Ingredient ingredient) {
        int ingIdx = ingredients.indexOf(ingredient);
        if (ingIdx < 0) return Optional.empty();

        AlchemicFormula deduced = null;
        int notExcludedCount = 0;

        for (int alcIdx = 0; alcIdx < alchemics.size(); alcIdx++) {
            // Se la cella è false, l'alchemico NON è stato escluso (non c'è la X)
            if (!excluded[alcIdx][ingIdx]) {
                notExcludedCount++;
                deduced = alchemics.get(alcIdx);
            }
        }

        // Se è rimasto esattamente un solo alchemico possibile, è la deduzione del giocatore!
        if (notExcludedCount == 1) {
            return Optional.of(deduced);
        }
        
        return Optional.empty();
    }

    public List<Ingredient> getIngredients() { return ingredients; }

    public List<AlchemicFormula> getAlchemics() { return alchemics; }
}
