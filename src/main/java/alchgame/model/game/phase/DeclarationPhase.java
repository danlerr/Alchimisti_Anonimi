package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeclarationPhase extends Phase {

    public DeclarationPhase(Board board, int startingPlayerIndex) {
        super(board, startingPlayerIndex);
    }

    @Override
    public List<Player> getPhaseOrder(List<Player> players) {
        List<Player> wakeUp = new ArrayList<>(board.getWakeUpOrder());
        Collections.reverse(wakeUp);
        return wakeUp;
    }

    public void declareAction(Player current, String actionSpaceId) {
        board.placeActionCube(actionSpaceId, current);
    }
}
