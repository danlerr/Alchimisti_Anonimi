package it.univaq.alchimisti_anonimi.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AlchemicFormula {
    private final List<Atom> atoms;

    public AlchemicFormula(List<Atom> atoms) {
        if (atoms == null || atoms.size() != 3) {
            throw new IllegalArgumentException("Formula must contain exactly 3 atoms");
        }
        this.atoms = List.copyOf(atoms);
    }

    public List<Atom> getAtoms() {
        return Collections.unmodifiableList(atoms);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlchemicFormula)) {
            return false;
        }
        AlchemicFormula that = (AlchemicFormula) o;
        return atoms.equals(that.atoms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atoms);
    }
}
