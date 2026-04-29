package alchgame.model.alchemy;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient other)) return false;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Ingredient{name='" + name + "'}";
    }
}
