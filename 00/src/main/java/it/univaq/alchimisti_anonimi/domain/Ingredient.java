package it.univaq.alchimisti_anonimi.domain;

import java.util.Objects;

public class Ingredient {
    private final String name;
    private final AlchemicFormula formula;

    public Ingredient(String name, AlchemicFormula formula) {
        this.name = Objects.requireNonNull(name);
        this.formula = Objects.requireNonNull(formula);
    }

    public String getName() {
        return name;
    }

    public AlchemicFormula getFormula() {
        return formula;
    }
}
