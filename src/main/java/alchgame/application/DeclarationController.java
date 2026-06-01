package alchgame.application;

import alchgame.model.board.Board;
import alchgame.model.game.Round;
import alchgame.model.player.Player;

import java.util.List;
import java.util.function.Supplier;

/**
 * Controller del caso d'uso "dichiara azioni" (UC02).
 */
public class DeclarationController {

    private final Supplier<Round> round;
    private final Board board;

    public DeclarationController(Supplier<Round> round, Board board) {
        this.round = round;
        this.board = board;
    }

    public List<String> getActionList() {
        return board.getActionSpaceIds();
    }

    public void declareAction(String actionSpaceId) {
        board.placeActionCube(actionSpaceId, getCurrentPlayer());
    }

    public Player getCurrentPlayer() {
        return round.get().getCurrentPlayer();
    }
}
