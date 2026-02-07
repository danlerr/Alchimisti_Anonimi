package it.univaq.alchimisti_anonimi.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PublicPlayerBoard {
    private final List<Potion> publishedPotions = new ArrayList<>();

    public void publishResult(Potion potion) {
        publishedPotions.add(potion);
    }

    public List<Potion> getPublishedPotions() {
        return Collections.unmodifiableList(publishedPotions);
    }
}
