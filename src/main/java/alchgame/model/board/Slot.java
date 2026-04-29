package alchgame.model.board;

import alchgame.model.player.Player;

/**
 * Slot — single slot of the OrderSpace (wake-up track).
 * Knows its taken state and the quantity of resources (ingredients + favors) it assigns.
 */
public class Slot {

    private final String id;
    private final Resources resources;
    private boolean taken = false;
    private Player assignedPlayer;

    public Slot(String id, Resources resources) {
        this.id = id;
        this.resources = resources;
    }

    public String getId() { return id; }

    public boolean isTaken() { return taken; }

    public void occupy(Player player) {
        if (taken)
            throw new IllegalStateException("Slot already occupied: " + id);
        taken = true;
        assignedPlayer = player;
    }

    public Resources getSlotResources() { return resources; }

    public Player getAssignedPlayer() { return assignedPlayer; }

    public void reset() {
        taken = false;
        assignedPlayer = null;
    }
}
