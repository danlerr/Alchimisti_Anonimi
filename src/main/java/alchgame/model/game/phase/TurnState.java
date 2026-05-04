package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.game.TurnPhase;
import alchgame.model.player.Player;

import java.util.List;

public interface TurnState {

    TurnPhase getPhase();

    List<Player> getPhaseOrder(List<Player> players, int startingPlayerIndex, Board board);

    TurnState advance(List<Player> players, Board board);
}
