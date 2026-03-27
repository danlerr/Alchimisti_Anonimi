package alchgame.model;

import java.util.HashMap;
import java.util.Map;

public class ResultsTriangle {

    private final Map<String, Potion> results = new HashMap<>();

    public void recordResult(Ingredient i1, Ingredient i2, Potion potion) {
        results.put(buildKey(i1, i2), potion);
    }

    public Potion getResult(Ingredient i1, Ingredient i2) {
        return results.get(buildKey(i1, i2));
    }

    private String buildKey(Ingredient i1, Ingredient i2) {
        String n1 = i1.getName(), n2 = i2.getName();
        return n1.compareTo(n2) <= 0 ? n1 + "|" + n2 : n2 + "|" + n1;
    }
}
