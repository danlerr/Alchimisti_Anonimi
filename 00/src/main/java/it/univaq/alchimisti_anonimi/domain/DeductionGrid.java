package it.univaq.alchimisti_anonimi.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeductionGrid {
    private final List<Observation> observations = new ArrayList<>();

    public void addObservation(IngredientCard ingredient1, IngredientCard ingredient2, Potion potion) {
        observations.add(new Observation(ingredient1, ingredient2, potion));
    }

    public List<Observation> getObservations() {
        return Collections.unmodifiableList(observations);
    }
}
