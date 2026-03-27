package alchgame.model;

import java.util.ArrayList;
import java.util.List;

public class PublicPlayerBoard {

    private final List<Potion> publishedResults = new ArrayList<>();

    public void publishExperimentResult(Potion potion) {
        publishedResults.add(potion);
        System.out.println("[PublicBoard] Pubblicato: " + potion);
    }

    public List<Potion> getPublishedResults() { return List.copyOf(publishedResults); }
}
