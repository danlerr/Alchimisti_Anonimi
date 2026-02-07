package it.univaq.alchimisti_anonimi.application;

import it.univaq.alchimisti_anonimi.domain.IngredientCard;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StartExperimentResult {
    private final boolean paymentRequired;
    private final List<IngredientCard> ingredients;

    private StartExperimentResult(boolean paymentRequired, List<IngredientCard> ingredients) {
        this.paymentRequired = paymentRequired;
        this.ingredients = ingredients == null ? List.of() : List.copyOf(ingredients);
    }

    public static StartExperimentResult paymentRequired() {
        return new StartExperimentResult(true, List.of());
    }

    public static StartExperimentResult withIngredients(List<IngredientCard> ingredients) {
        Objects.requireNonNull(ingredients);
        return new StartExperimentResult(false, ingredients);
    }

    public boolean isPaymentRequired() {
        return paymentRequired;
    }

    public List<IngredientCard> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }
}
