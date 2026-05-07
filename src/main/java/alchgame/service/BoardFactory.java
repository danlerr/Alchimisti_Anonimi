package alchgame.service;

import alchgame.model.alchemy.Ingredient;
import alchgame.model.board.ActionSpace;
import alchgame.model.board.Board;
import alchgame.model.board.CardDeck;
import alchgame.model.board.Favor;
import alchgame.model.board.OrderSpace;
import alchgame.model.board.Resources;
import alchgame.model.board.Slot;
import alchgame.resources.GameConfig;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardFactory {

    public Board createBoard(List<Ingredient> ingredients) {
        OrderSpace orderSpace = createOrderSpace();
        Map<String, ActionSpace> actionSpaces = createActionSpaces();
        CardDeck<Ingredient> ingredientDeck = createIngredientDeck(ingredients);
        CardDeck<Favor> favorDeck = createFavorDeck();

        return new Board(actionSpaces, orderSpace, ingredientDeck, favorDeck);
    }

    private OrderSpace createOrderSpace() {
        Map<String, Slot> slots = new HashMap<>();

        for (GameConfig.SlotSpec spec : GameConfig.SLOTS) {
            Resources resources = new Resources(
                    spec.ingredientCount(),
                    spec.favorCount()
            );
            slots.put(spec.id(), new Slot(spec.id(), resources));
        }

        List<String> slotOrder = GameConfig.SLOTS.stream()
                .map(GameConfig.SlotSpec::id)
                .toList();

        return new OrderSpace(slots, slotOrder);
    }

    private Map<String, ActionSpace> createActionSpaces() {
        Map<String, ActionSpace> actionSpaces = new HashMap<>();

        for (String id : GameConfig.ACTION_ORDER) {
            actionSpaces.put(id, new ActionSpace(id));
        }

        return actionSpaces;
    }

    private CardDeck<Ingredient> createIngredientDeck(List<Ingredient> ingredients) {
        List<Ingredient> cards = new ArrayList<>();

        for (int copy = 0; copy < GameConfig.INGREDIENT_DECK_COPIES; copy++) {
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

        for (int i = 0; i < GameConfig.FAVOR_DECK_SIZE; i++) {
            deck.add(new Favor("favor-" + i));
        }

        return new CardDeck<>(deck);
    }
}
