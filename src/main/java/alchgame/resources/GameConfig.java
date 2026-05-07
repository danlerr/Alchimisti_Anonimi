package alchgame.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameConfig {

    public record SlotSpec(String id, int ingredientCount, int favorCount) { }
    public record AtomSpec(String color, String size, String sign) {}
    public record FormulaSpec(List<AtomSpec> atoms) {}

    public static final String TARGET_STUDENT_ID;
    public static final String SELF_ID;
    public static final int    MIN_PLAYERS;
    public static final int    MAX_PLAYERS;
    public static final int    STARTING_GOLD;
    public static final int    STARTING_REPUTATION;
    public static final int    STARTING_ACTION_CUBES;
    public static final int    STARTING_INGREDIENTS;
    public static final int    INGREDIENT_DECK_COPIES;
    public static final int    FAVOR_DECK_SIZE;
    public static final int    TOTAL_ROUNDS;
    public static final List<String>      INGREDIENT_NAMES;
    public static final List<FormulaSpec> FORMULA_SPECS;
    public static final List<SlotSpec>    SLOTS;
    public static final String AS_FORAGE         = "forage";
    public static final String AS_TRANSMUTE      = "transmute";
    public static final String AS_EXPERIMENT     = "experiment";
    public static final String AS_SELL_POTION    = "sell-potion";
    
    public static final List<String> ACTION_LIST = List.of(
        AS_FORAGE, AS_TRANSMUTE, AS_SELL_POTION, AS_EXPERIMENT
    );

    static {
        try (InputStream is = openConfig()) {
            if (is == null) throw new IllegalStateException("game-config.properties non trovato");
            Properties props = new Properties();
            props.load(is);

            TARGET_STUDENT_ID   = props.getProperty("targetStudentId");
            SELF_ID      = props.getProperty("selfId");
            MIN_PLAYERS         = Integer.parseInt(props.getProperty("minPlayers"));
            MAX_PLAYERS         = Integer.parseInt(props.getProperty("maxPlayers"));
            STARTING_GOLD       = Integer.parseInt(props.getProperty("startingGold"));
            STARTING_REPUTATION = Integer.parseInt(props.getProperty("startingReputation"));

            INGREDIENT_NAMES = List.of(props.getProperty("ingredients").split(","));

            String[] colors = props.getProperty("formula.colors").split(",");
            int count = Integer.parseInt(props.getProperty("formula.count"));
            List<FormulaSpec> formulaSpecs = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                List<AtomSpec> atomSpecs = new ArrayList<>();
                for (String color : colors) {
                    String size = props.getProperty("formula." + i + "." + color + ".size");
                    String sign = props.getProperty("formula." + i + "." + color + ".sign");
                    atomSpecs.add(new AtomSpec(color, size, sign));
                }
                formulaSpecs.add(new FormulaSpec(List.copyOf(atomSpecs)));
            }
            FORMULA_SPECS = List.copyOf(formulaSpecs);

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
            INGREDIENT_DECK_COPIES = Integer.parseInt(props.getProperty("ingredientDeckCopies", "6"));

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static InputStream openConfig() throws Exception {
        InputStream is = GameConfig.class.getClassLoader().getResourceAsStream("game-config.properties");
        if (is != null) return is;
        File f = new File("src/main/java/alchgame/resources/game-config.properties");
        return f.exists() ? new FileInputStream(f) : null;
    }
}
