package alchgame.model;

import java.util.ArrayList;
import java.util.List;

public class ResultsTriangle {
    private final List<String> results = new ArrayList<>();

    public void recordResult(Ingredient ingredient1, Ingredient ingredient2, Potion potion) {
        String record = ingredient1.getName() + "+" + ingredient2.getName() + "=" + potion.getColor() + potion.getSign();
        results.add(record);
    }

    public List<String> getResults() {
        return results;
    }
}
