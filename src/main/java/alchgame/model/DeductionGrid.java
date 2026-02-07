package alchgame.model;

import java.util.ArrayList;
import java.util.List;

public class DeductionGrid {
    private String annotations;
    private final List<String> observations = new ArrayList<>();

    public DeductionGrid(String annotations) {
        this.annotations = annotations;
    }

    public String getAnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }

    public void addObservation(Ingredient ingredient1, Ingredient ingredient2, Potion potion) {
        String record = ingredient1.getName() + "+" + ingredient2.getName() + "=" + potion.getColor() + potion.getSign();
        observations.add(record);
    }

    public List<String> getObservations() {
        return observations;
    }
}
