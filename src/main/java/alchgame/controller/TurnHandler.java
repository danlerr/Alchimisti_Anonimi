package alchgame.controller;

import alchgame.model.Board;
import alchgame.model.Player;
import alchgame.model.Resources;
import alchgame.service.GameContext;

/**
 * TurnHandler — controller per le operazioni di sistema del caso d'uso "svolgere un turno"
 * (chooseSlot, declareAction).
 */
public class TurnHandler {

    private final GameContext gameContext;
    private final Board board;

    public TurnHandler(GameContext gameContext, Board board) {
        this.gameContext = gameContext;
        this.board = board;
    }

    public Resources chooseSlot(String slotID) {
        Player player = gameContext.getCurrentPlayer();
        board.setPlayer(slotID, player);
        Resources resources = board.getResources(slotID);
        board.pickCard(player, resources);
        return resources;
    }

    public void declareAction(String actionSpaceId) {
        Player player = gameContext.getCurrentPlayer();
        board.setAction(actionSpaceId, player);
    }
}
