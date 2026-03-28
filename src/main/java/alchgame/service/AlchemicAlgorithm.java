package alchgame.service;

import alchgame.model.AlchemicFormula;
import alchgame.model.Color;
import alchgame.model.Ingredient;
import alchgame.model.Potion;
import alchgame.model.Sign;

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
        Color color = deriveColor(ingredient1.getFormula(), ingredient2.getFormula());
        Sign sign  = deriveSign(ingredient1.getFormula(),  ingredient2.getFormula());
        return Potion.createPotion(color, sign);
    }

    private Color deriveColor(AlchemicFormula f1, AlchemicFormula f2) {
        // Logica di dominio: da implementare secondo le regole del gioco
        return f1.getAtoms().get(0).getColor();
    }

    private Sign deriveSign(AlchemicFormula f1, AlchemicFormula f2) {
        long positive = f1.getAtoms().stream()
                .filter(a -> "+".equals(a.getSign())).count();
        return positive >= 2 ? Sign.POSITIVE : Sign.NEGATIVE;
    }
}
