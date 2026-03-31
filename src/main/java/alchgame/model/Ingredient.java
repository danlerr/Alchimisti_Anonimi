package alchgame.model;

/**
 * Ingredient — rappresenta una carta ingrediente del laboratorio privato.
 * La mappatura verso l'AlchemicFormula è nascosta nel sistema (AlchemicMapping),
 * i giocatori non possono accedervi direttamente.
 */
public class Ingredient {

    private final String name;

    public Ingredient(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public String toString() {
        return "Ingredient{name='" + name + "'}";
    }
}
