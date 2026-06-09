package alchgame.model.factory;

import java.util.List;

public record FormulaSpec(List<AtomSpec> atoms) {

    public FormulaSpec {
        atoms = List.copyOf(atoms);
    }
}
