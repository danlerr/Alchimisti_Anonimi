package alchgame.model.factory;

import alchgame.config.GameConfig;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.board.ActionSpace;
import alchgame.model.board.Board;
import alchgame.model.board.CardDeck;
import alchgame.model.board.Favor;
import alchgame.model.board.OrderSpace;
import alchgame.model.board.Slot;
import alchgame.model.board.favorEffect.AssistantEffect;
import alchgame.model.board.favorEffect.ErboristEffect;
import alchgame.model.board.slotReward.FavorReward;
import alchgame.model.board.slotReward.IngredientReward;
import alchgame.model.board.slotReward.SlotReward;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory responsabile della costruzione del tabellone di gioco.
 *
 * Riceve dati gia interpretati dalla configurazione e assembla le componenti
 * condivise del board: tracciato di ordine, spazi azione, mazzo ingredienti e
 * mazzo favori.
 */
public class BoardFactory {

    private final List<SlotSpec> slotSpecs;
    private final List<String> actionOrder;
    private final int ingredientDeckCopies;
    private final int favorDeckSize;
    private final int erboristIngredients;
    private final int assistantCubes;

    public BoardFactory(
            List<SlotSpec> slotSpecs,
            List<String> actionOrder,
            int ingredientDeckCopies,
            int favorDeckSize,
            int erboristIngredients,
            int assistantCubes
    ) {
        this.slotSpecs = List.copyOf(slotSpecs);
        this.actionOrder = List.copyOf(actionOrder);
        this.ingredientDeckCopies = ingredientDeckCopies;
        this.favorDeckSize = favorDeckSize;
        this.erboristIngredients = erboristIngredients;
        this.assistantCubes = assistantCubes;
    }

    public Board createBoard(List<Ingredient> ingredients) {
        OrderSpace orderSpace = createOrderSpace();
        Map<String, ActionSpace> actionSpaces = createActionSpaces();
        CardDeck<Ingredient> ingredientDeck = createIngredientDeck(ingredients);
        CardDeck<Favor> favorDeck = createFavorDeck();
        int actionCubeCost = GameConfig.ACTION_CUBE_COST;

        return new Board(actionSpaces, orderSpace, ingredientDeck, favorDeck, actionCubeCost);
    }

    private OrderSpace createOrderSpace() {
        Map<String, Slot> slots = new HashMap<>();

        for (SlotSpec spec : slotSpecs) {
            List<SlotReward> rewards = new ArrayList<>();
            if (spec.ingredientCount() > 0) rewards.add(new IngredientReward(spec.ingredientCount()));
            if (spec.favorCount() > 0)      rewards.add(new FavorReward(spec.favorCount()));
            slots.put(spec.id(), new Slot(spec.id(), rewards));
        }

        List<String> slotOrder = slotSpecs.stream()
                .map(SlotSpec::id)
                .toList();

        return new OrderSpace(slots, slotOrder);
    }

    private Map<String, ActionSpace> createActionSpaces() {
        Map<String, ActionSpace> actionSpaces = new LinkedHashMap<>();

        for (String id : actionOrder) {
            actionSpaces.put(id, new ActionSpace(id));
        }

        return actionSpaces;
    }

    private CardDeck<Ingredient> createIngredientDeck(List<Ingredient> ingredients) {
        List<Ingredient> cards = new ArrayList<>();

        for (int copy = 0; copy < ingredientDeckCopies; copy++) {
            for (Ingredient ingredient : ingredients) {
                cards.add(new Ingredient(ingredient.getName()));
            }
        }

        Collections.shuffle(cards);
        Deque<Ingredient> deck = new ArrayDeque<>(cards);

        return new CardDeck<>(deck);
    }

    private CardDeck<Favor> createFavorDeck() {
        List<Favor> cards = new ArrayList<>();

        int half = favorDeckSize / 2;
        for (int i = 0; i < half; i++)
            cards.add(new Favor("Erborista", new ErboristEffect(erboristIngredients)));
        for (int i = half; i < favorDeckSize; i++)
            cards.add(new Favor("Assistant", new AssistantEffect(assistantCubes)));

        Collections.shuffle(cards);
        return new CardDeck<>(new ArrayDeque<>(cards));
    }
}
