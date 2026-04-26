package alchgame.model;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Board — game board. Contains action spaces, the OrderSpace (wake-up track)
 * and the ingredient and favor card decks.
 */
public class Board {

    private final Map<String, ActionSpace> actionSpaces;
    private final OrderSpace orderSpace;
    private final Deque<Ingredient> ingredientDeck;
    private final Deque<Favor> favorDeck;

    public Board(Map<String, ActionSpace> actionSpaces,
                 OrderSpace orderSpace,
                 Deque<Ingredient> ingredientDeck,
                 Deque<Favor> favorDeck) {
        this.actionSpaces = new HashMap<>(actionSpaces);
        this.orderSpace = orderSpace;
        this.ingredientDeck = ingredientDeck;
        this.favorDeck = favorDeck;
    }

    public ActionSpace getActionSpace(String actionSpaceId) {
        ActionSpace space = actionSpaces.get(actionSpaceId);
        if (space == null)
            throw new IllegalArgumentException("Action space not found: " + actionSpaceId);
        return space;
    }

    public void setAction(String actionSpaceId, Player player) {
        getActionSpace(actionSpaceId).placeActionCube(player);
    }

    public void dealIngredients(Player player, int count) {
        for (int i = 0; i < count; i++) {
            Ingredient ingredient = ingredientDeck.poll();
            if (ingredient == null)
                throw new IllegalStateException("Ingredient deck exhausted.");
            player.addIngredient(ingredient);
        }
    }

    public List<Player> getWakeUpOrder() {
        return orderSpace.getWakeUpOrder();
    }

    public List<String> getAvailableSlotIds() {
        return orderSpace.getAvailableSlotIds();
    }

    public void resetRound() {
        actionSpaces.values().forEach(ActionSpace::reset);
        orderSpace.reset();
    }

    public Optional<Favor> drawFavor() {
        return Optional.ofNullable(favorDeck.poll());
    }

    public void dealFavors(Player player, int count) {
        for (int i = 0; i < count; i++) {
            Favor favor = favorDeck.poll();
            if (favor == null)
                throw new IllegalStateException("Favor deck exhausted.");
            player.addFavor(favor);
        }
    }

    // ---- OrderSpace -------------------------------------------------------

    public Resources assignOrderSlot(String orderSlotID, Player player) {
        orderSpace.setPlayer(orderSlotID, player);
        Resources resources = orderSpace.getResources(orderSlotID);
        dealIngredients(player, resources.ingredientCount());
        dealFavors(player, resources.favorCount());
        return resources;
    }
}
