package it.univaq.alchimisti_anonimi.services;

import it.univaq.alchimisti_anonimi.domain.IngredientCard;
import it.univaq.alchimisti_anonimi.domain.Potion;
import it.univaq.alchimisti_anonimi.domain.PotionColor;
import it.univaq.alchimisti_anonimi.domain.PotionSign;

import java.util.Objects;

public class AlchemicAlgorithm {
    public Potion computePotion(IngredientCard ingredient1, IngredientCard ingredient2) {
        Objects.requireNonNull(ingredient1);
        Objects.requireNonNull(ingredient2);
        int hash = Objects.hash(ingredient1.getId(), ingredient2.getId());
        int colorIndex = Math.abs(hash) % PotionColor.values().length;
        int signIndex = Math.abs(hash / 3) % PotionSign.values().length;
        PotionColor color = PotionColor.values()[colorIndex];
        PotionSign sign = PotionSign.values()[signIndex];
        return new Potion(color, sign);
    }
}
