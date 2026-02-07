package it.univaq.alchimisti_anonimi.domain;

import java.util.Objects;

public class IngredientCard {
    private final String id;
    private final Ingredient ingredient;

    public IngredientCard(String id, Ingredient ingredient) {
        this.id = Objects.requireNonNull(id);
        this.ingredient = Objects.requireNonNull(ingredient);
    }

    public String getId() {
        return id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }
}
