package alchgame.service;

import alchgame.model.alchemy.Ingredient;
import alchgame.model.board.ActionSpace;
import alchgame.model.board.Board;
import alchgame.model.board.CardDeck;
import alchgame.model.board.Favor;
import alchgame.model.board.OrderSpace;
import alchgame.model.board.Resources;
import alchgame.model.board.Slot;
import alchgame.resources.SlotSpec;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
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

    public BoardFactory(
            List<SlotSpec> slotSpecs,
            List<String> actionOrder,
            int ingredientDeckCopies,
            int favorDeckSize
    ) {
        this.slotSpecs = List.copyOf(slotSpecs);
        this.actionOrder = List.copyOf(actionOrder);
        this.ingredientDeckCopies = ingredientDeckCopies;
        this.favorDeckSize = favorDeckSize;
    }

    public Board createBoard(List<Ingredient> ingredients) {
        OrderSpace orderSpace = createOrderSpace();
        Map<String, ActionSpace> actionSpaces = createActionSpaces();
        CardDeck<Ingredient> ingredientDeck = createIngredientDeck(ingredients);
        CardDeck<Favor> favorDeck = createFavorDeck();

        return new Board(actionSpaces, orderSpace, ingredientDeck, favorDeck);
    }

    private OrderSpace createOrderSpace() {
        Map<String, Slot> slots = new HashMap<>();

        for (SlotSpec spec : slotSpecs) {
            Resources resources = new Resources(
                    spec.ingredientCount(),
                    spec.favorCount()
            );
            slots.put(spec.id(), new Slot(spec.id(), resources));
        }

        List<String> slotOrder = slotSpecs.stream()
                .map(SlotSpec::id)
                .toList();

        return new OrderSpace(slots, slotOrder);
    }

    private Map<String, ActionSpace> createActionSpaces() {
        Map<String, ActionSpace> actionSpaces = new HashMap<>();

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
        Deque<Favor> deck = new ArrayDeque<>();

        for (int i = 0; i < favorDeckSize; i++) {
            deck.add(new Favor("favor-" + i));
        }

        return new CardDeck<>(deck);
    }
}
