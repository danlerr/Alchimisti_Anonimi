package alchgame.controller;

import alchgame.model.player.Player;
import alchgame.model.board.Resources;
import alchgame.model.game.GameSession;

/**
 * TurnHandler — controller per le operazioni di sistema del caso d'uso "svolgere un turno"
 * (chooseSlot, declareAction).
 */
public class TurnController {

    private final GameSession game;

    public TurnController(GameSession alchGame) {
        this.game = alchGame;
    }

    public Resources chooseSlot(String orderSlotID) {
        Player player = game.getCurrentPlayer();
        return game.getBoard().assignOrderSlot(orderSlotID, player);
    }

    public void declareAction(String actionSpaceId) {
        Player player = game.getCurrentPlayer();
        game.getBoard().placeActionCube(actionSpaceId, player);
    }
}
