package alchgame.model.board.slotreward;

import alchgame.model.board.Board;
import alchgame.model.board.SlotReward;
import alchgame.model.player.Player;

public class IngredientReward implements SlotReward {

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
