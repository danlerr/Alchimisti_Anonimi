package alchgame;

import alchgame.model.*;

import java.util.List;

public class GameConfig {

    public static final String TARGET_STUDENT_ID = "student-1";
    public static final String TARGET_SELF_ID    = "self";

    static final List<String> INGREDIENT_NAMES = List.of(
        "Felce", "Mandragora", "Artiglio", "Fiore",
        "Fungo", "Rospo", "Piuma", "Scorpione"
    );

    static final List<AlchemicFormula> FORMULAS = List.of(
        formula(Color.RED, Size.BIG,   Sign.POSITIVE,  Color.GREEN, Size.BIG,   Sign.POSITIVE,  Color.BLUE, Size.BIG,   Sign.POSITIVE),
        formula(Color.RED, Size.BIG,   Sign.NEGATIVE,  Color.GREEN, Size.BIG,   Sign.NEGATIVE,  Color.BLUE, Size.BIG,   Sign.NEGATIVE),
        formula(Color.RED, Size.SMALL, Sign.NEGATIVE,  Color.GREEN, Size.SMALL, Sign.POSITIVE,  Color.BLUE, Size.BIG,   Sign.NEGATIVE),
        formula(Color.RED, Size.SMALL, Sign.POSITIVE,  Color.GREEN, Size.SMALL, Sign.NEGATIVE,  Color.BLUE, Size.BIG,   Sign.POSITIVE),
        formula(Color.RED, Size.SMALL, Sign.NEGATIVE,  Color.GREEN, Size.BIG,   Sign.POSITIVE,  Color.BLUE, Size.SMALL, Sign.POSITIVE),
        formula(Color.RED, Size.SMALL, Sign.POSITIVE,  Color.GREEN, Size.BIG,   Sign.NEGATIVE,  Color.BLUE, Size.SMALL, Sign.NEGATIVE),
        formula(Color.RED, Size.BIG,   Sign.POSITIVE,  Color.GREEN, Size.SMALL, Sign.POSITIVE,  Color.BLUE, Size.SMALL, Sign.NEGATIVE),
        formula(Color.RED, Size.BIG,   Sign.NEGATIVE,  Color.GREEN, Size.SMALL, Sign.NEGATIVE,  Color.BLUE, Size.BIG,   Sign.POSITIVE)
    );

    private static AlchemicFormula formula(
            Color c1, Size s1, Sign sg1,
            Color c2, Size s2, Sign sg2,
            Color c3, Size s3, Sign sg3) {
        return new AlchemicFormula(List.of(
            new Atom(c1, s1, sg1),
            new Atom(c2, s2, sg2),
            new Atom(c3, s3, sg3)
        ));
    }
}
