package alchgame;

import alchgame.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameConfig {

    public static final String TARGET_STUDENT_ID;
    public static final String TARGET_SELF_ID;
    public static final int    STARTING_GOLD;
    public static final int    STARTING_REPUTATION;

    static final List<String>          INGREDIENT_NAMES;
    static final List<AlchemicFormula> FORMULAS;

    static {
        try (InputStream is = openConfig()) {
            if (is == null) throw new IllegalStateException("game-config.properties non trovato");
            Properties props = new Properties();
            props.load(is);

            TARGET_STUDENT_ID   = props.getProperty("targetStudentId");
            TARGET_SELF_ID      = props.getProperty("targetSelfId");
            STARTING_GOLD       = Integer.parseInt(props.getProperty("startingGold"));
            STARTING_REPUTATION = Integer.parseInt(props.getProperty("startingReputation"));

            INGREDIENT_NAMES = List.of(props.getProperty("ingredients").split(","));

            int count = Integer.parseInt(props.getProperty("formula.count"));
            List<AlchemicFormula> formulas = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                List<Atom> atoms = new ArrayList<>();
                for (Color c : Color.values()) {
                    Size size = Size.valueOf(props.getProperty("formula." + i + "." + c.name() + ".size"));
                    Sign sign = Sign.valueOf(props.getProperty("formula." + i + "." + c.name() + ".sign"));
                    atoms.add(new Atom(c, size, sign));
                }
                formulas.add(new AlchemicFormula(atoms));
            }
            FORMULAS = List.copyOf(formulas);

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static InputStream openConfig() throws Exception {
        InputStream is = GameConfig.class.getClassLoader().getResourceAsStream("game-config.properties");
        if (is != null) return is;
        File f = new File("src/main/resources/game-config.properties");
        return f.exists() ? new FileInputStream(f) : null;
    }
}
