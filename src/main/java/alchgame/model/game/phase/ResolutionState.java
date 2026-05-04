package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.game.TurnPhase;
import alchgame.model.player.Player;

import java.util.List;

public class ResolutionState implements TurnState {

    @Override
    public TurnPhase getPhase() {
        return TurnPhase.RESOLUTION;
    }

    @Override
    public List<Player> getPhaseOrder(List<Player> players, int startingPlayerIndex, Board board) {
        return board.getWakeUpOrder();
    }

    @Override
    public TurnState advance(List<Player> players, Board board) {
        throw new IllegalStateException("RESOLUTION è l'ultima fase del turno.");
    }
}
