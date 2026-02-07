package alchgame.model;

public class Ingredient {
    private final String name;
    private final AlchemicFormula formula;

    public Ingredient(String name, AlchemicFormula formula) {
        this.name = name;
        this.formula = formula;
    }

    public String getName() {
        return name;
    }

    public AlchemicFormula getFormula() {
        return formula;
    }
}
