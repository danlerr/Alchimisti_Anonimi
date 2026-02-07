package it.univaq.alchimisti_anonimi.domain;

import java.util.Objects;

public class Potion {
    private final PotionColor color;
    private final PotionSign sign;

    public Potion(PotionColor color, PotionSign sign) {
        this.color = Objects.requireNonNull(color);
        this.sign = Objects.requireNonNull(sign);
    }

    public PotionColor getColor() {
        return color;
    }

    public PotionSign getSign() {
        return sign;
    }
}
