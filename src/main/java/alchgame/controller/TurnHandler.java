package alchgame.controller;

import alchgame.model.Player;
import alchgame.model.Resources;
import alchgame.service.AlchGame;

/**
 * TurnHandler — controller per le operazioni di sistema del caso d'uso "svolgere un turno"
 * (chooseSlot, declareAction).
 */
public class TurnHandler {

    private final AlchGame alchGame;

    public TurnHandler(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    public Resources chooseSlot(String orderSlotID) {
        Player player = alchGame.getCurrentPlayer();
        return alchGame.getBoard().assignOrderSlot(orderSlotID, player);
    }

    public void declareAction(String actionSpaceId) {
        Player player = alchGame.getCurrentPlayer();
        alchGame.getBoard().setAction(actionSpaceId, player);
    }
}
