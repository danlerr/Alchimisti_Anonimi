package alchgame.model;

import java.util.ArrayList;
import java.util.List;

public class DeductionGrid {

    private final List<String> annotations;

    public DeductionGrid() {
        this.annotations = new ArrayList<>();
    }

    public List<String> getAnnotations() { return List.copyOf(annotations); }

    public void addObservation(Ingredient i1, Ingredient i2, Potion potion) {
        annotations.add(i1.getName() + " + " + i2.getName() + " → " + potion);
    }
}
