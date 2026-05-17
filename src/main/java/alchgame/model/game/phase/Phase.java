package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.List;

public abstract class Phase {

    private final RoundPhase id;
    protected final Board board;

    protected Phase(RoundPhase id, Board board) {
        this.id = id;
        this.board = board;
    }

    public RoundPhase getPhase() {
        return id;
    }

    public abstract List<Player> getPhaseOrder(List<Player> players, int startingPlayerIndex);
}
