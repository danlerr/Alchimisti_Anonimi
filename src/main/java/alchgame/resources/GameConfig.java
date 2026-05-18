package alchgame.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Punto centrale di accesso alla configurazione statica dell'applicazione.
 *
 * Carica una sola volta il file properties e conserva i parametri scalari
 * globali usati per configurare partita, risorse iniziali e ordine delle azioni.
 * Espone inoltre metodi di lettura per le sezioni strutturate, restituendo
 * solo dati grezzi di configurazione, senza costruire oggetti del dominio.
 */
public class GameConfig {

    public static final Properties PROPERTIES;

    public static final String TARGET_STUDENT_ID;
    public static final String SELF_ID;
    public static final int MIN_PLAYERS;
    public static final int MAX_PLAYERS;
    public static final int STARTING_GOLD;
    public static final int STARTING_REPUTATION;
    public static final int STARTING_ACTION_CUBES;
    public static final int STARTING_INGREDIENTS;
    public static final int INGREDIENT_DECK_COPIES;
    public static final int FAVOR_DECK_SIZE;
    public static final int TOTAL_ROUNDS;

    public static final String AS_FORAGE = "forage";
    public static final String AS_TRANSMUTE = "transmute";
    public static final String AS_BUY_ARTIFACT = "buy-artifact";
    public static final String AS_EXPERIMENT = "experiment";
    public static final String AS_PUBLISH_THEORY = "publish-theory";
    public static final String AS_DEBUNK_THEORY = "debunk-theory";

    public static final List<String> ACTION_ORDER = List.of(
        AS_FORAGE, AS_TRANSMUTE, AS_BUY_ARTIFACT, AS_EXPERIMENT,
        AS_PUBLISH_THEORY, AS_DEBUNK_THEORY
    );

    public static final List<String> RESOLUTION_ORDER = List.of(
        AS_FORAGE, AS_TRANSMUTE, AS_EXPERIMENT
    );

    static {
        try (InputStream is = openConfig()) {
            if (is == null) throw new IllegalStateException("game-config.properties non trovato");
            Properties props = new Properties();
            props.load(is);
            PROPERTIES = props;

            TARGET_STUDENT_ID = props.getProperty("targetStudentId");
            SELF_ID = props.getProperty("selfId");
            MIN_PLAYERS = Integer.parseInt(props.getProperty("minPlayers"));
            MAX_PLAYERS = Integer.parseInt(props.getProperty("maxPlayers"));
            STARTING_GOLD = Integer.parseInt(props.getProperty("startingGold"));
            STARTING_REPUTATION = Integer.parseInt(props.getProperty("startingReputation"));
            FAVOR_DECK_SIZE = Integer.parseInt(props.getProperty("favor.count"));
            STARTING_ACTION_CUBES = Integer.parseInt(props.getProperty("startingActionCubes"));
            TOTAL_ROUNDS = Integer.parseInt(props.getProperty("totalRounds"));
            STARTING_INGREDIENTS = Integer.parseInt(props.getProperty("startingIngredients"));
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

    public static List<String> getIngredientNames() {
        return List.of(PROPERTIES.getProperty("ingredients").split(","));
    }

    public static List<FormulaSpec> getFormulaSpecs() {
        int count = Integer.parseInt(PROPERTIES.getProperty("formula.count"));
        List<FormulaSpec> formulas = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            formulas.add(createFormulaSpec(i));
        }

        return List.copyOf(formulas);
    }

    public static List<SlotSpec> getSlotSpecs() {
        int slotCount = Integer.parseInt(PROPERTIES.getProperty("slot.count"));
        List<SlotSpec> slots = new ArrayList<>();

        for (int i = 0; i < slotCount; i++) {
            String id = PROPERTIES.getProperty("slot." + i + ".id");
            int ingredientCount = Integer.parseInt(PROPERTIES.getProperty("slot." + i + ".ingredients"));
            int favorCount = Integer.parseInt(PROPERTIES.getProperty("slot." + i + ".favors"));
            slots.add(new SlotSpec(id, ingredientCount, favorCount));
        }

        return List.copyOf(slots);
    }

    private static FormulaSpec createFormulaSpec(int index) {
        List<AtomSpec> atoms = new ArrayList<>();

        for (String color : getFormulaColors()) {
            String size = PROPERTIES.getProperty("formula." + index + "." + color + ".size");
            String sign = PROPERTIES.getProperty("formula." + index + "." + color + ".sign");
            atoms.add(new AtomSpec(color, size, sign));
        }

        return new FormulaSpec(atoms);
    }

    private static List<String> getFormulaColors() {
        return List.of(PROPERTIES.getProperty("formula.colors").split(","));
    }
}
