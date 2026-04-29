package alchgame.controller;

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
        return game.chooseSlot(orderSlotID);
    }

    public void declareAction(String actionSpaceId) {
        game.declareAction(actionSpaceId);
    }
}
