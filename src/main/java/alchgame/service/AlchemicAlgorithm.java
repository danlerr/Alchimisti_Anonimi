package alchgame.service;

import alchgame.model.Atom;
import alchgame.model.AlchemicFormula;
import alchgame.model.Color;
import alchgame.model.Ingredient;
import alchgame.model.Potion;
import alchgame.model.Sign;
import alchgame.model.Size;

/**
 * AlchemicAlgorithm — calcola la Potion risultante dalla combinazione
 * di due Ingredient consultando la mappatura nascosta AlchemicMapping.
 */
public class AlchemicAlgorithm {

    private final AlchemicMapping alchemicMapping;

    public AlchemicAlgorithm(AlchemicMapping alchemicMapping) {
        this.alchemicMapping = alchemicMapping;
    }

    /**
     * Calcola la pozione combinando le formule dei due ingredienti.
     * Per ogni colore, se gli atomi corrispondenti hanno lo stesso segno il colore "reagisce".
     * Tra i colori che reagiscono, vince quello con l'atomo BIG.
     * Il segno della pozione è il segno comune degli atomi del colore vincitore.
     * (SD ConductExperiment — step 1.1 / 1.1.1)
     */
    public Potion computePotion(Ingredient ingredient1, Ingredient ingredient2) {
        AlchemicFormula f1 = alchemicMapping.getFormula(ingredient1);
        AlchemicFormula f2 = alchemicMapping.getFormula(ingredient2);

        Color resultColor = null;
        Sign  resultSign  = null;
        boolean resultIsBig = false;

        for (Color c : Color.values()) {
            Atom a1 = atomByColor(f1, c);
            Atom a2 = atomByColor(f2, c);
            if (a1 == null || a2 == null) continue;
            if (a1.getSign() != a2.getSign()) continue;

            boolean isBig = (a1.getSize() == Size.BIG);
            if (resultColor == null || (isBig && !resultIsBig)) {
                resultColor = c;
                resultSign  = a1.getSign();
                resultIsBig = isBig;
            }
        }

        if (resultColor == null) {
            resultColor = f1.getAtoms().get(0).getColor();
            resultSign  = Sign.NEGATIVE;
        }

        return Potion.createPotion(resultColor, resultSign);
    }

    private Atom atomByColor(AlchemicFormula f, Color c) {
        return f.getAtoms().stream()
                .filter(a -> a.getColor() == c)
                .findFirst().orElse(null);
    }
}
