package alchgame.controller;

import alchgame.model.Board;
import alchgame.model.Player;
import alchgame.service.GameContext;

/**
 * DeclareActionHandler — controller per l'operazione di sistema declareAction.
 */
public class TurnHandler {

    private final GameContext gameContext;
    private final Board board;

    public TurnHandler(GameContext gameContext, Board board) {
        this.gameContext = gameContext;
        this.board = board;
    }

    public void declareAction(String actionSpaceId) {
        Player player = gameContext.getCurrentPlayer();
        board.setAction(actionSpaceId, player);
    }
}
