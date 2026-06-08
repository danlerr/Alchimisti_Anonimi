package alchgame.model.board.favorEffect;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

@FunctionalInterface
public interface FavorEffect {
    void apply(Player player, Board board);
}
