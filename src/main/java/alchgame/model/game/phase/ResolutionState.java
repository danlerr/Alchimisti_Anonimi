package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.game.RoundPhase;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ResolutionState implements RoundState {

    @Override
    public RoundPhase getPhase() {
        return RoundPhase.RESOLUTION;
    }

    @Override
    public List<Player> getPhaseOrder(List<Player> players, int startingPlayerIndex, Board board) {
        return board.getWakeUpOrder();
    }

    @Override
    public RoundState advance(List<Player> players, Board board) {
        throw new IllegalStateException("RESOLUTION è l'ultima fase del turno.");
    }

    public List<Player> getResolutionOrder(Board board, String actionSpaceId) {
        List<Player> declared = board.getActionSpace(actionSpaceId).getDeclaredPlayers();
        List<Player> order = new ArrayList<>();
        for (Player player : board.getWakeUpOrder()) {
            if (declared.contains(player)) {
                order.add(player);
            }
        }
        return order;
    }
}
