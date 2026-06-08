package alchgame.model.board.slotReward;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

public interface SlotReward {
    void apply(Player player, Board board);
    String describe();
}
