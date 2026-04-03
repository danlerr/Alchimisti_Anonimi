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

    /**
     * Aggiorna la griglia dopo un esperimento: esclude per entrambi gli ingredienti
     * gli alchemici la cui componente del colore della pozione ha segno opposto a
     * quello della pozione ottenuta.
     *
     * Esempio: pozione (+ROSSA) → si escludono tutti gli alchemici con atomo ROSSO
     * negativo, perché nessuno dei due ingredienti può avere una componente rossa
     * negativa se la pozione risultante è positiva.
     */
    public void addObservation(Ingredient i1, Ingredient i2, Potion potion) {
        if (potion.isNeutral()) {
            // Pozione neutrale: le formule di i1 e i2 si neutralizzano a vicenda.
            // Per i1: esclude ogni formula F il cui neutralizzatore non è più possibile per i2.
            // Per i2: esclude ogni formula F il cui neutralizzatore non è più possibile per i1.
            List<AlchemicFormula> possible1 = new ArrayList<>(getPossibleAlchemics(i1));
            List<AlchemicFormula> possible2 = new ArrayList<>(getPossibleAlchemics(i2));
            for (AlchemicFormula f : possible1) {
                if (!possible2.contains(findNeutralizer(f)))
                    exclude(i1, f);
            }
            for (AlchemicFormula f : possible2) {
                if (!possible1.contains(findNeutralizer(f)))
                    exclude(i2, f);
            }
        } else {
            for (AlchemicFormula formula : alchemics) {
                for (Atom atom : formula.getAtoms()) {
                    if (atom.getColor() == potion.getColor() && atom.getSign() != potion.getSign()) {
                        if (ingredients.contains(i1)) exclude(i1, formula);
                        if (ingredients.contains(i2)) exclude(i2, formula);
                        break;
                    }
                }
            }
        }
    }

    private AlchemicFormula findNeutralizer(AlchemicFormula formula) {
        return alchemics.stream()
                .filter(f -> f.isNeutralizerOf(formula))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Neutralizzatore non trovato per: " + formula));
    }

    public List<Ingredient> getIngredients() { return ingredients; }
    public List<AlchemicFormula> getAlchemics() { return alchemics; }

    /**
     * Restituisce una riga descrittiva per ogni ingrediente che ha almeno un alchemico
     * escluso, utile per la visualizzazione nel laboratorio privato.
     */
    public List<String> getExclusionsSummary() {
        List<String> summary = new ArrayList<>();
        for (int ingIdx = 0; ingIdx < ingredients.size(); ingIdx++) {
            List<String> excludedAlcs = new ArrayList<>();
            for (int alcIdx = 0; alcIdx < alchemics.size(); alcIdx++) {
                if (excluded[alcIdx][ingIdx])
                    excludedAlcs.add(formulaLabel(alchemics.get(alcIdx)));
            }
            if (!excludedAlcs.isEmpty())
                summary.add(ingredients.get(ingIdx).getName() + ": esclusi " + String.join(", ", excludedAlcs));
        }
        return summary;
    }

    /**
     * Rappresentazione leggibile di una formula: es. "[Rosso+ Grande, Verde- Piccolo, Blu+ Grande]"
     */
    private static String formulaLabel(AlchemicFormula formula) {
        StringBuilder sb = new StringBuilder();
        for (Atom atom : formula.getAtoms()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(switch (atom.getColor()) {
                case RED   -> "Rosso";
                case GREEN -> "Verde";
                case BLUE  -> "Blu";
            });
            sb.append(atom.getSign() == Sign.POSITIVE ? "+" : "-");
            sb.append(" ");
            sb.append(atom.getSize() == Size.BIG ? "Grande" : "Piccolo");
        }
        return "[" + sb + "]";
    }
}
