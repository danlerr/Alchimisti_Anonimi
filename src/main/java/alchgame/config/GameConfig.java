package alchgame.config;

import alchgame.model.factory.AtomSpec;
import alchgame.model.factory.FormulaSpec;
import alchgame.model.factory.SlotSpec;

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

    private static final Properties PROPERTIES;

    private static final String TARGET_STUDENT_ID;
    private static final int STUDENT_UNHAPPY_COST;
    private static final String SELF_ID;
    private static final int MIN_PLAYERS;
    private static final int MAX_PLAYERS;
    private static final int STARTING_GOLD;
    private static final int STARTING_REPUTATION;
    private static final int STARTING_ACTION_CUBES;
    private static final int STARTING_INGREDIENTS;
    private static final int INGREDIENT_DECK_COPIES;
    private static final int FAVOR_DECK_SIZE;
    private static final int TOTAL_ROUNDS;

    private static final String AS_FORAGE = "forage";
    private static final String AS_TRANSMUTE = "transmute";
    private static final String AS_EXPERIMENT = "experiment";

    private static final int FORAGE_YIELD;
    private static final int TRASMUTE_GOLD;
    private static final int ACTION_CUBE_COST;

    private static final int EFFECT_BLUE_POS;
    private static final int EFFECT_BLUE_NEG;
    private static final int EFFECT_RED_POS;
    private static final int EFFECT_RED_NEG;
    private static final int EFFECT_GREEN_POS;

    private static final int FAVOR_ERBORIST_INGREDIENTS;
    private static final int FAVOR_ASSISTANT_CUBES;

    private static final List<String> ACTION_ORDER = List.of(
        AS_FORAGE, AS_TRANSMUTE, AS_EXPERIMENT
    );

    static {
        try (InputStream is = openConfig()) {
            if (is == null) throw new IllegalStateException("game-config.properties non trovato");
            Properties props = new Properties();
            props.load(is);
            PROPERTIES = props;

            TARGET_STUDENT_ID = props.getProperty("targetStudentId");
            STUDENT_UNHAPPY_COST = Integer.parseInt(props.getProperty("rules.student.unhappy_cost", "1"));
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

            FORAGE_YIELD = Integer.parseInt(props.getProperty("rules.forage.yield", "1"));
            TRASMUTE_GOLD = Integer.parseInt(props.getProperty("rules.trasmute.gold", "1"));
            ACTION_CUBE_COST = Integer.parseInt(props.getProperty("rules.action_cube_cost", "1"));

            EFFECT_BLUE_POS = Integer.parseInt(props.getProperty("rules.effect.blue_pos", "1"));
            EFFECT_BLUE_NEG = Integer.parseInt(props.getProperty("rules.effect.blue_neg", "-1"));
            EFFECT_RED_POS  = Integer.parseInt(props.getProperty("rules.effect.red_pos", "1"));
            EFFECT_RED_NEG  = Integer.parseInt(props.getProperty("rules.effect.red_neg", "-1"));
            EFFECT_GREEN_POS= Integer.parseInt(props.getProperty("rules.effect.green_pos", "1"));

            FAVOR_ERBORIST_INGREDIENTS = Integer.parseInt(props.getProperty("rules.favor.erborist.ingredients", "3"));
            FAVOR_ASSISTANT_CUBES       = Integer.parseInt(props.getProperty("rules.favor.assistant.cubes", "1"));

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static InputStream openConfig() throws Exception {
        InputStream is = GameConfig.class.getClassLoader().getResourceAsStream("game-config.properties");
        if (is != null) return is;
        File f = new File("src/main/java/alchgame/config/game-config.properties");
        return f.exists() ? new FileInputStream(f) : null;
    }

    public static String getTargetStudentId() {
        return TARGET_STUDENT_ID;
    }

    public static int getStudentUnhappyCost() {
        return STUDENT_UNHAPPY_COST;
    }

    public static String getSelfId() {
        return SELF_ID;
    }

    public static int getMinPlayers() {
        return MIN_PLAYERS;
    }

    public static int getMaxPlayers() {
        return MAX_PLAYERS;
    }

    public static int getStartingGold() {
        return STARTING_GOLD;
    }

    public static int getStartingReputation() {
        return STARTING_REPUTATION;
    }

    public static int getStartingActionCubes() {
        return STARTING_ACTION_CUBES;
    }

    public static int getStartingIngredients() {
        return STARTING_INGREDIENTS;
    }

    public static int getIngredientDeckCopies() {
        return INGREDIENT_DECK_COPIES;
    }

    public static int getFavorDeckSize() {
        return FAVOR_DECK_SIZE;
    }

    public static int getTotalRounds() {
        return TOTAL_ROUNDS;
    }

    public static String getActionForage() {
        return AS_FORAGE;
    }

    public static String getActionTransmute() {
        return AS_TRANSMUTE;
    }

    public static String getActionExperiment() {
        return AS_EXPERIMENT;
    }

    public static int getForageYield() {
        return FORAGE_YIELD;
    }

    public static int getTrasmuteGold() {
        return TRASMUTE_GOLD;
    }

    public static int getActionCubeCost() {
        return ACTION_CUBE_COST;
    }

    public static int getEffectBluePos() {
        return EFFECT_BLUE_POS;
    }

    public static int getEffectBlueNeg() {
        return EFFECT_BLUE_NEG;
    }

    public static int getEffectRedPos() {
        return EFFECT_RED_POS;
    }

    public static int getEffectRedNeg() {
        return EFFECT_RED_NEG;
    }

    public static int getEffectGreenPos() {
        return EFFECT_GREEN_POS;
    }

    public static int getFavorErboristIngredients() {
        return FAVOR_ERBORIST_INGREDIENTS;
    }

    public static int getFavorAssistantCubes() {
        return FAVOR_ASSISTANT_CUBES;
    }

    public static List<String> getActionOrder() {
        return ACTION_ORDER;
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
