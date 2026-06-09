package alchgame.model.board;

import alchgame.model.board.slotReward.SlotReward;
import alchgame.model.player.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OrderSpace — wake-up track. Aggregates Slots indexed by id.
 */
public class OrderSpace {

    private final Map<String, Slot> slots;
    private final List<String> slotOrder;

    public OrderSpace(Map<String, Slot> slots, List<String> slotOrder) {
        this.slots = new HashMap<>(slots);
        this.slotOrder = List.copyOf(slotOrder);
    }

    public void setPlayer(String orderSlotID, Player player) {
        getSlot(orderSlotID).occupy(player);
    }

    public List<SlotReward> getRewards(String orderSlotID) {
        return getSlot(orderSlotID).getRewards();
    }

    public List<Player> getWakeUpOrder() {
        return slotOrder.stream()
            .map(slots::get)
            .filter(Slot::isTaken)
            .map(Slot::getAssignedPlayer)
            .toList();
    }

    public List<String> getAvailableSlotIds() {
        return slotOrder.stream()
            .filter(id -> !slots.get(id).isTaken())
            .toList();
    }

    public Map<String, Player> getAssignments() {
        Map<String, Player> result = new LinkedHashMap<>();
        for (String id : slotOrder) {
            Slot slot = slots.get(id);
            result.put(id, slot.isTaken() ? slot.getAssignedPlayer() : null);
        }
        return result;
    }

    public void reset() {
        slots.values().forEach(Slot::reset);
    }

    private Slot getSlot(String orderSlotID) {
        Slot slot = slots.get(orderSlotID);
        if (slot == null)
            throw new IllegalArgumentException("Slot not found: " + orderSlotID);
        return slot;
    }

    public List<String> getAllSlotIds() {
        return slotOrder; 
    }
}
