package alchgame.model.alchemy;

import java.util.List;

public class AlchemicFormula {

    /** Composta da un atomo per ciascun colore definito in {@link Color}. */
    private final List<Atom> atoms;

    public AlchemicFormula(List<Atom> atoms) {
        int expectedSize = Color.values().length;
        if (atoms == null || atoms.size() != expectedSize)
            throw new IllegalArgumentException("Una formula alchemica deve avere esattamente " + expectedSize + " atomi.");
        long distinctColors = atoms.stream().map(Atom::getColor).distinct().count();
        if (distinctColors != expectedSize)
            throw new IllegalArgumentException("Una formula alchemica deve avere un atomo per ciascun colore definito.");
        this.atoms = List.copyOf(atoms);
    }

    public List<Atom> getAtoms() { return atoms; }

    /** Restituisce l'atomo del colore indicato, null se non presente. */
    public Atom getAtomByColor(Color c) {
        return atoms.stream().filter(a -> a.getColor() == c).findFirst().orElse(null);
    }

    /** Restituisce true se questa formula neutralizza {@code other}:
     *  ogni atomo ha lo stesso colore ma segno opposto rispetto all'altro. */
    public boolean isNeutralizerOf(AlchemicFormula other) {
        for (Atom a : atoms) {
            Atom counterpart = other.getAtomByColor(a.getColor());
            if (counterpart == null || counterpart.getSign() == a.getSign()) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AlchemicFormula{atoms=" + atoms + "}";
    }
}
