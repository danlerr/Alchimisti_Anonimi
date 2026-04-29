package alchgame.model.player;

import alchgame.model.alchemy.Potion;

import java.util.ArrayList;
import java.util.List;

public class PublicPlayerBoard {

    private final List<Potion> publishedResults = new ArrayList<>();

    public void publishExperimentResult(Potion potion) {
        publishedResults.add(potion);
    }

    public List<Potion> getPublishedResults() { return List.copyOf(publishedResults); }
}
