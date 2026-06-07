package alchgame.model.board;

import alchgame.model.board.slotReward.SlotRewardStrategy;
import alchgame.model.player.Player;

import java.util.List;

public class Slot {

    private final String id;
    private final List<SlotRewardStrategy> rewards;
    private boolean taken = false;
    private Player assignedPlayer;

    public Slot(String id, List<SlotRewardStrategy> rewards) {
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

    public List<SlotRewardStrategy> getRewards() { return rewards; }

    public Player getAssignedPlayer() { return assignedPlayer; }

    public void reset() {
        taken = false;
        assignedPlayer = null;
    }
}
