package alchgame.model.player;

import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.Potion;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResultsTriangle {

    private final Map<Set<Ingredient>, Potion> results = new HashMap<>();

    public void recordResult(Ingredient i1, Ingredient i2, Potion potion) {
        results.put(Set.of(i1, i2), potion);
    }

    public Potion getResult(Ingredient i1, Ingredient i2) {
        return results.get(Set.of(i1, i2));
    }

    public boolean hasResult(Ingredient i1, Ingredient i2) {
        return results.containsKey(Set.of(i1, i2));
    }

    public Map<Set<Ingredient>, Potion> getAllResults() {
        return Map.copyOf(results);
    }
}
