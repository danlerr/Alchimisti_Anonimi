package alchgame.model.board.slotReward;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

public class IngredientReward implements SlotRewardStrategy {

    private final int count;

    public IngredientReward(int count) {
        this.count = count;
    }

    @Override
    public void apply(Player player, Board board) {
        board.dealIngredients(player, count);
    }

    @Override
    public String describe() {
        return count + " ingrediente/i";
    }
}
