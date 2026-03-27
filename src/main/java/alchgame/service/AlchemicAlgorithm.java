package com.alchemy.service;

import com.alchemy.model.AlchemicFormula;
import com.alchemy.model.Ingredient;
import com.alchemy.model.Potion;

/**
 * AlchemicAlgorithm — calcola la Potion risultante dalla combinazione
 * di due Ingredient leggendo le loro AlchemicFormula.
 */
public class AlchemicAlgorithm {

    /**
     * Calcola la pozione combinando le formule dei due ingredienti.
     * (SD ConductExperiment — step 1.1 / 1.1.1)
     */
    public Potion computePotion(Ingredient ingredient1, Ingredient ingredient2) {
        String color = deriveColor(ingredient1.getFormula(), ingredient2.getFormula());
        String sign  = deriveSign(ingredient1.getFormula(),  ingredient2.getFormula());
        return Potion.createPotion(color, sign);
    }

    private String deriveColor(AlchemicFormula f1, AlchemicFormula f2) {
        // Logica di dominio: da implementare secondo le regole del gioco
        return f1.getAtoms().get(0).getColor();
    }

    private String deriveSign(AlchemicFormula f1, AlchemicFormula f2) {
        long positive = f1.getAtoms().stream()
                .filter(a -> "+".equals(a.getSign())).count();
        return positive >= 2 ? "+" : "-";
    }
}
