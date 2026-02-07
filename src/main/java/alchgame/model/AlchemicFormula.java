package alchgame.model;

import java.util.List;

public class AlchemicFormula {
    private final List<Atom> atoms;

    public AlchemicFormula(List<Atom> atoms) {
        this.atoms = atoms;
    }

    public List<Atom> getAtoms() {
        return atoms;
    }

    public String getSignForColor(String color) {
        if (color == null) {
            return null;
        }
        for (Atom atom : atoms) {
            if (color.equalsIgnoreCase(atom.getColor())) {
                return atom.getSign();
            }
        }
        return null;
    }
}
