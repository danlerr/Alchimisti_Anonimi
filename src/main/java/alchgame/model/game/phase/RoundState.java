package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.game.RoundPhase;
import alchgame.model.player.Player;

import java.util.List;

public interface RoundState {

    RoundPhase getPhase();

    List<Player> getPhaseOrder(List<Player> players, int startingPlayerIndex, Board board);

    RoundState advance(List<Player> players, Board board);

    default List<Player> getResolutionOrder(Board board, String actionSpaceId) {
        throw new IllegalStateException("Operazione ammessa solo durante RESOLUTION.");
    }
}
