package alchgame.model.player;

import alchgame.model.board.Board;

@FunctionalInterface
public interface DelayedEffect {
    void apply(Player player, Board board);
}
