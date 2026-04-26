package alchgame.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Board — game board. Contains action spaces, the OrderSpace (wake-up track)
 * and the ingredient and favor card decks.
 */
public class Board {

    private final Map<String, ActionSpace> actionSpaces;
    private final OrderSpace orderSpace;
    private final Deck<Ingredient> ingredientDeck;
    private final Deck<Favor> favorDeck;

    public Board(Map<String, ActionSpace> actionSpaces,
                 OrderSpace orderSpace,
                 Deck<Ingredient> ingredientDeck,
                 Deck<Favor> favorDeck) {
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

    public void dealIngredients(Player player, int count) {
        for (int i = 0; i < count; i++) {
            player.addIngredient(ingredientDeck.draw());
        }
    }

    public void dealFavors(Player player, int count) {
        for (int i = 0; i < count; i++) {
            player.addFavor(favorDeck.draw());
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
