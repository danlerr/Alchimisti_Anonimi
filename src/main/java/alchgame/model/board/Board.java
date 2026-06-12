package alchgame.model.board;

import alchgame.model.alchemy.Ingredient;
import alchgame.model.board.slotReward.SlotReward;
import alchgame.model.player.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Board {

    // --- Fields ---

    private final Map<String, ActionSpace> actionSpaces;
    private final OrderSpace orderSpace;
    private final Deck<Ingredient> ingredientDeck;
    private final Deck<Favor> favorDeck;
    private final int actionCubeCost;

    // --- Constructor ---

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

    // --- Action spaces ---

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

    public List<Player> getDeclaredPlayers(String ActionSpaceId) {
        return getActionSpace(ActionSpaceId).getDeclaredPlayers();
    }

    // --- Order space ---

    public List<String> getAllSlotIds() {
        return orderSpace.getAllSlotIds();
    }

    public List<String> getAvailableSlotIds() {
        return orderSpace.getAvailableSlotIds();
    }

    public List<Player> getWakeUpOrder() {
        return orderSpace.getWakeUpOrder();
    }

    public Map<String, Player> getOrderAssignments() {
        return orderSpace.getAssignments();
    }

    public void assignOrderSlot(String orderSlotID, Player player) {
        orderSpace.setPlayer(orderSlotID, player);
    }

    public List<SlotReward> assignSlotResources(String orderSlotID, Player player) {
        List<SlotReward> rewards = orderSpace.getRewards(orderSlotID);
        rewards.forEach(r -> r.apply(player, this));
        return rewards;
    }

    public List<String> getSlotRewardDescriptions(String slotId) {
        return orderSpace.getRewards(slotId).stream()
            .map(SlotReward::describe)
            .toList();
    }

    // --- Decks ---

    public List<Ingredient> dealIngredients(Player player, int count) {
        List<Ingredient> dealt = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            Ingredient ing = ingredientDeck.draw();
            player.addIngredient(ing);
            dealt.add(ing);
        }
        return dealt;
    }

    public void dealFavors(Player player, int count) {
        for (int i = 0; i < count; i++) {
            player.addFavor(favorDeck.draw());
        }
    }

    // --- Lifecycle ---

    public void resetBoard() {
        actionSpaces.values().forEach(ActionSpace::reset);
        orderSpace.reset();
    }
}
