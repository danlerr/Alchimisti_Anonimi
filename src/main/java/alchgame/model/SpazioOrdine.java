package alchgame.model;

import java.util.HashMap;
import java.util.Map;

/**
 * SpazioOrdine — tracciato di risveglio. Aggrega gli Slot indicizzati per id.
 */
public class SpazioOrdine {

    private final Map<String, Slot> slots;

    public SpazioOrdine(Map<String, Slot> slots) {
        this.slots = new HashMap<>(slots);
    }

    public void setPlayer(String slotID, Player player) {
        Slot slot = getSlot(slotID);
        slot.setTaken(true);
        slot.assign(player);
    }

    public Resources getResources(String slotID) {
        return getSlot(slotID).getSlotResources();
    }

    private Slot getSlot(String slotID) {
        Slot slot = slots.get(slotID);
        if (slot == null)
            throw new IllegalArgumentException("Slot non trovato: " + slotID);
        return slot;
    }
}
