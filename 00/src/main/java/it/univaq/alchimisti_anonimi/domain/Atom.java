package it.univaq.alchimisti_anonimi.domain;

import java.util.Objects;

public class Atom {
    private final AtomColor color;
    private final AtomSize size;
    private final AtomSign sign;

    public Atom(AtomColor color, AtomSize size, AtomSign sign) {
        this.color = Objects.requireNonNull(color);
        this.size = Objects.requireNonNull(size);
        this.sign = Objects.requireNonNull(sign);
    }

    public AtomColor getColor() {
        return color;
    }

    public AtomSize getSize() {
        return size;
    }

    public AtomSign getSign() {
        return sign;
    }
}
