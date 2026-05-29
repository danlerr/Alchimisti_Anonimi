package alchgame.controller;

import alchgame.model.board.Board;
import alchgame.model.game.AlchGame;
import alchgame.model.player.Player;

import java.util.List;

/**
 * Controller del caso d'uso "dichiara azioni" (UC02).
 */
public class DeclarationController {

    private final AlchGame alchGame;
    private final Board board;

    public DeclarationController(AlchGame alchGame, Board board) {
        this.alchGame = alchGame;
        this.board = board;
    }

    public List<String> getActionList() {
        return alchGame.getCurrentRound().getBoard().getActionSpaceIds();
    }

    public void declareAction(String actionSpaceId) {
        board.placeActionCube(actionSpaceId, getCurrentPlayer());
    }

    public Player getCurrentPlayer() {
        return alchGame.getCurrentRound().orderPhase().getCurrentPlayer();
    }
}
