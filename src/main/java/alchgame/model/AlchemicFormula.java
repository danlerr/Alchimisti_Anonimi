package alchgame.model;

import java.util.List;

public class AlchemicFormula {

    /** Composta esattamente da 3 atomi. */
    private final List<Atom> atoms;

    public AlchemicFormula(List<Atom> atoms) {
        if (atoms == null || atoms.size() != 3) {
            throw new IllegalArgumentException("Una AlchemicFormula deve avere esattamente 3 atomi.");
        }
        this.atoms = List.copyOf(atoms);
    }

    public List<Atom> getAtoms() { return atoms; }

    @Override
    public String toString() {
        return "AlchemicFormula{atoms=" + atoms + "}";
    }
}
