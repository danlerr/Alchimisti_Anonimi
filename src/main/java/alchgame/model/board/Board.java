package alchgame.model.board;

import alchgame.model.alchemy.Ingredient;
import alchgame.model.player.Player;

import java.util.LinkedHashMap;
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
    private final int actionCubeCost;

    public Board(Map<String, ActionSpace> actionSpaces,
                 OrderSpace orderSpace,
                 Deck<Ingredient> ingredientDeck,
                 Deck<Favor> favorDeck,
                int actionCubeCost) {
        this.actionSpaces = new LinkedHashMap<>(actionSpaces);
        this.orderSpace = orderSpace;
        this.ingredientDeck = ingredientDeck;
        this.favorDeck = favorDeck;
        this.actionCubeCost = actionCubeCost;
    }

    public List<String> getActionSpaceIds() {
        return List.copyOf(actionSpaces.keySet());
    }

    public ActionSpace getActionSpace(String actionSpaceId) {
        ActionSpace space = actionSpaces.get(actionSpaceId);
        if (space == null)
            throw new IllegalArgumentException("Action space not found: " + actionSpaceId);
        return space;
    }

    public void placeActionCube(String actionSpaceId, Player player) {
        ActionSpace space = getActionSpace(actionSpaceId);
        player.removeActionCube(this.actionCubeCost);
        space.addDeclaredPlayer(player);
    }

    public List<Player> getWakeUpOrder() {
        return orderSpace.getWakeUpOrder();
    }

    public List<String> getAvailableSlotIds() {
        return orderSpace.getAvailableSlotIds();
    }

    public Map<String, Player> getOrderAssignments() {
        return orderSpace.getAssignments();
    }

    public void resetBoard() {
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

    public void assignOrderSlot(String orderSlotID, Player player) {
        orderSpace.setPlayer(orderSlotID, player);
    }

    public Resources assignSlotResources(String orderSlotID, Player player) {
        Resources resources = orderSpace.getResources(orderSlotID);
        dealIngredients(player, resources.ingredientCount());
        dealFavors(player, resources.favorCount());
        return resources;
    }
}
