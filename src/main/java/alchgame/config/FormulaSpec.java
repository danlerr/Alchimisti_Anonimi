package alchgame.config;

import java.util.List;

/**
 * Dato di configurazione che descrive una formula alchemica in modo grezzo.
 */
public record FormulaSpec(List<AtomSpec> atoms) {

    public FormulaSpec {
        atoms = List.copyOf(atoms);
    }
}
