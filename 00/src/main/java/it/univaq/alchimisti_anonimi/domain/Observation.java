package it.univaq.alchimisti_anonimi.domain;

import java.util.Objects;

public class Observation {
    private final IngredientCard ingredient1;
    private final IngredientCard ingredient2;
    private final Potion potion;

    public Observation(IngredientCard ingredient1, IngredientCard ingredient2, Potion potion) {
        this.ingredient1 = Objects.requireNonNull(ingredient1);
        this.ingredient2 = Objects.requireNonNull(ingredient2);
        this.potion = Objects.requireNonNull(potion);
    }

    public IngredientCard getIngredient1() {
        return ingredient1;
    }

    public IngredientCard getIngredient2() {
        return ingredient2;
    }

    public Potion getPotion() {
        return potion;
    }
}
