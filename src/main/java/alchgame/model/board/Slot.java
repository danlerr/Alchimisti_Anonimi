package alchgame.model.board;

import alchgame.model.board.slotReward.SlotReward;
import alchgame.model.player.Player;

import java.util.List;

public class Slot {

    private final String id;
    private final List<SlotReward> rewards;
    private boolean taken = false;
    private Player assignedPlayer;

    public Slot(String id, List<SlotReward> rewards) {
        this.id = id;
        this.rewards = List.copyOf(rewards);
    }

    public String getId() { return id; }

    public boolean isTaken() { return taken; }

    public void occupy(Player player) {
        if (taken)
            throw new IllegalStateException("Slot already occupied: " + id);
        taken = true;
        assignedPlayer = player;
    }

    public List<SlotReward> getRewards() { return rewards; }

    public Player getAssignedPlayer() { return assignedPlayer; }

    public void reset() {
        taken = false;
        assignedPlayer = null;
    }
}
