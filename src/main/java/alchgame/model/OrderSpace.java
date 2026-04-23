package alchgame.model;

import java.util.HashMap;
import java.util.Map;

/**
 * OrderSpace — wake-up track. Aggregates Slots indexed by id.
 */
public class OrderSpace {

    private final Map<String, Slot> slots;

    public OrderSpace(Map<String, Slot> slots) {
        this.slots = new HashMap<>(slots);
    }

    public void setPlayer(String orderSlotID, Player player) {
        getSlot(orderSlotID).occupy(player);
    }

    public Resources getResources(String orderSlotID) {
        return getSlot(orderSlotID).getSlotResources();
    }

    private Slot getSlot(String orderSlotID) {
        Slot slot = slots.get(orderSlotID);
        if (slot == null)
            throw new IllegalArgumentException("Slot not found: " + orderSlotID);
        return slot;
    }
}
