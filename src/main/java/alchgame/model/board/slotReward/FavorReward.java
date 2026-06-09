package alchgame.model.board.slotReward;

import alchgame.model.board.Board;
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
        return count + " fav";
    }
}
