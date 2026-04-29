package alchgame.service;

import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.alchemy.Ingredient;

import java.util.Map;

/**
 * AlchemicMapping — mantiene la mappatura nascosta Ingrediente → AlchemicFormula.
 * Corrisponde all'associazione "randomizzata all'avvio" del domain model.
 * Solo AlchemicAlgorithm la consulta; i giocatori non vi hanno accesso.
 */
public class AlchemicMapping {

    private final Map<Ingredient, AlchemicFormula> mapping;

    public AlchemicMapping(Map<Ingredient, AlchemicFormula> mapping) {
        this.mapping = Map.copyOf(mapping);
    }

    public AlchemicFormula getFormulaByIngredient(Ingredient ingredient) {
        AlchemicFormula f = mapping.get(ingredient);
        if (f == null) throw new IllegalArgumentException("Nessuna formula alchemica per: " + ingredient);
        return f;
    }

}
