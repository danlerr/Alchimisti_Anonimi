package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.List;

public abstract class Phase {

    protected final Board board;
    protected final int startingPlayerIndex;

    protected Phase(Board board, int startingPlayerIndex) {
        this.board = board;
        this.startingPlayerIndex = startingPlayerIndex;
    }

    public abstract List<Player> getPhaseOrder(List<Player> players);
}
