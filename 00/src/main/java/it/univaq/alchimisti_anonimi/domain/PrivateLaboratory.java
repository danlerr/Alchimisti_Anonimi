package it.univaq.alchimisti_anonimi.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PrivateLaboratory {
    private final Map<String, IngredientCard> cards = new LinkedHashMap<>();
    private final DeductionGrid deductionGrid;
    private final ResultsTriangle resultsTriangle;

    public PrivateLaboratory(DeductionGrid deductionGrid, ResultsTriangle resultsTriangle) {
        this.deductionGrid = Objects.requireNonNull(deductionGrid);
        this.resultsTriangle = Objects.requireNonNull(resultsTriangle);
    }

    public void addIngredientCard(IngredientCard card) {
        cards.put(card.getId(), card);
    }

    public IngredientCard getIngredientCard(String id) {
        IngredientCard card = cards.get(id);
        if (card == null) {
            throw new IllegalArgumentException("Unknown ingredient card: " + id);
        }
        return card;
    }

    public List<IngredientCard> getIngredients() {
        return Collections.unmodifiableList(new ArrayList<>(cards.values()));
    }

    public void updateLab(IngredientCard ingredient1, IngredientCard ingredient2, Potion potion) {
        discard(ingredient1, ingredient2);
        deductionGrid.addObservation(ingredient1, ingredient2, potion);
        resultsTriangle.recordResult(ingredient1, ingredient2, potion);
    }

    private void discard(IngredientCard ingredient1, IngredientCard ingredient2) {
        cards.remove(ingredient1.getId());
        cards.remove(ingredient2.getId());
    }

    public DeductionGrid getDeductionGrid() {
        return deductionGrid;
    }

    public ResultsTriangle getResultsTriangle() {
        return resultsTriangle;
    }
}
