package it.univaq.alchimisti_anonimi.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultsTriangle {
    private final List<Observation> results = new ArrayList<>();

    public void recordResult(IngredientCard ingredient1, IngredientCard ingredient2, Potion potion) {
        results.add(new Observation(ingredient1, ingredient2, potion));
    }

    public List<Observation> getResults() {
        return Collections.unmodifiableList(results);
    }
}
