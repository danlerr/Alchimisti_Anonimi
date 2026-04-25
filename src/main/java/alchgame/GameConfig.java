package alchgame;

import alchgame.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameConfig {

    public record SlotSpec(String id, int ingredientCount, int favorCount) { }

    public static final String TARGET_STUDENT_ID;
    public static final String TARGET_SELF_ID;
    public static final int    STARTING_GOLD;
    public static final int    STARTING_REPUTATION;
    public static final int    STARTING_ACTION_CUBES;
    public static final int    STARTING_INGREDIENTS;
    public static final int    FAVOR_DECK_SIZE;
    public static final int    TOTAL_ROUNDS;

    public static final List<String>          INGREDIENT_NAMES;
    public static final List<AlchemicFormula> FORMULAS;
    public static final List<SlotSpec>        SLOTS;

    public static final String AS_FORAGE         = "forage";
    public static final String AS_TRANSMUTE      = "transmute";
    public static final String AS_BUY_ARTIFACT   = "buy-artifact";
    public static final String AS_EXPERIMENT     = "experiment";
    public static final String AS_SELL_POTION    = "sell-potion";
    public static final String AS_PUBLISH_THEORY = "publish-theory";
    public static final String AS_DEBUNK_THEORY  = "debunk-theory";

    public static final List<String> ACTION_ORDER = List.of(
        AS_FORAGE, AS_TRANSMUTE, AS_BUY_ARTIFACT, AS_EXPERIMENT,
        AS_SELL_POTION, AS_PUBLISH_THEORY, AS_DEBUNK_THEORY
    );

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

            int slotCount = Integer.parseInt(props.getProperty("slot.count"));
            List<SlotSpec> slots = new ArrayList<>();
            for (int i = 0; i < slotCount; i++) {
                String id     = props.getProperty("slot." + i + ".id");
                int ingCount  = Integer.parseInt(props.getProperty("slot." + i + ".ingredients"));
                int favCount  = Integer.parseInt(props.getProperty("slot." + i + ".favors"));
                slots.add(new SlotSpec(id, ingCount, favCount));
            }
            SLOTS = List.copyOf(slots);

            FAVOR_DECK_SIZE        = Integer.parseInt(props.getProperty("favor.count"));
            STARTING_ACTION_CUBES  = Integer.parseInt(props.getProperty("startingActionCubes"));
            TOTAL_ROUNDS           = Integer.parseInt(props.getProperty("totalRounds"));
            STARTING_INGREDIENTS   = Integer.parseInt(props.getProperty("startingIngredients"));

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
