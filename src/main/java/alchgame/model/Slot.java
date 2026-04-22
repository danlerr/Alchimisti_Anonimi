package alchgame.model;

/**
 * Slot — singolo slot del tracciato di risveglio (SpazioOrdine).
 * Conosce lo stato taken e la quantità di risorse (ingredienti + favori) che assegna.
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

    public void setTaken(boolean taken) {
        if (taken && this.taken)
            throw new IllegalStateException("Slot già occupato: " + id);
        this.taken = taken;
    }

    public Resources getSlotResources() { return resources; }

    public void assign(Player player) { this.assignedPlayer = player; }

    public Player getAssignedPlayer() { return assignedPlayer; }
}
