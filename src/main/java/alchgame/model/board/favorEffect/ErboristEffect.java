package alchgame.model.board.favorEffect;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

public class ErboristEffect implements FavorEffect {

    private final int ingredientCount;

    public ErboristEffect(int ingredientCount) {
        this.ingredientCount = ingredientCount;
    }

    @Override
    public void apply(Player player, Board board) {
        board.dealIngredients(player, ingredientCount);
    }
}
