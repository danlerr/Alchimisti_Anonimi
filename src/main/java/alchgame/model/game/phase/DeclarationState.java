package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.game.RoundPhase;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeclarationState implements RoundState {

    @Override
    public RoundPhase getPhase() {
        return RoundPhase.DECLARATION;
    }

    @Override
    public List<Player> getPhaseOrder(List<Player> players, int startingPlayerIndex, Board board) {
        List<Player> wakeUp = new ArrayList<>(board.getWakeUpOrder());
        Collections.reverse(wakeUp);
        return wakeUp;
    }

    @Override
    public RoundState advance(List<Player> players, Board board) {
        return new ResolutionState();
    }
}
