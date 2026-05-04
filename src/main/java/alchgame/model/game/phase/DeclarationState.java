package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.game.TurnPhase;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeclarationState implements TurnState {

    @Override
    public TurnPhase getPhase() {
        return TurnPhase.DECLARATION;
    }

    @Override
    public List<Player> getPhaseOrder(List<Player> players, int startingPlayerIndex, Board board) {
        List<Player> wakeUp = new ArrayList<>(board.getWakeUpOrder());
        Collections.reverse(wakeUp);
        return wakeUp;
    }

    @Override
    public TurnState advance(List<Player> players, Board board) {
        return new ResolutionState();
    }
}
