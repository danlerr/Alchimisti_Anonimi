package alchgame.model.board.slotreward;

import alchgame.model.board.Board;
import alchgame.model.board.SlotReward;
import alchgame.model.player.Player;

public class FavorReward implements SlotReward {

    private final int count;

    public FavorReward(int count) {
        this.count = count;
    }

    @Override
    public void apply(Player player, Board board) {
        board.dealFavors(player, count);
    }

    @Override
    public String describe() {
        return count + " carta/e favore";
    }
}
