package alchgame.model;

import java.util.List;

public class AlchemicFormula {

    /** Composta esattamente da 3 atomi, uno per ciascun colore (RED, GREEN, BLUE). */
    private final List<Atom> atoms;

    public AlchemicFormula(List<Atom> atoms) {
        if (atoms == null || atoms.size() != 3)
            throw new IllegalArgumentException("Una formula alchemica deve avere esattamente 3 atomi.");
        long distinctColors = atoms.stream().map(Atom::getColor).distinct().count();
        if (distinctColors != 3)
            throw new IllegalArgumentException("Una formula alchemica deve avere un atomo per ciascun colore (RED, GREEN, BLUE).");
        this.atoms = List.copyOf(atoms);
    }

    public List<Atom> getAtoms() { return atoms; }

    /** Restituisce true se questa formula neutralizza {@code other}:
     *  ogni atomo ha lo stesso colore ma segno opposto rispetto all'altro. */
    public boolean isNeutralizerOf(AlchemicFormula other) {
        for (Atom a : atoms) {
            Atom counterpart = other.getAtoms().stream()
                    .filter(o -> o.getColor() == a.getColor())
                    .findFirst().orElse(null);
            if (counterpart == null || counterpart.getSign() == a.getSign()) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AlchemicFormula{atoms=" + atoms + "}";
    }
}
