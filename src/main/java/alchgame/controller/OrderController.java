package alchgame.controller;

import alchgame.model.board.Board;
import alchgame.model.board.Resources;
import alchgame.model.game.AlchGame;
import alchgame.model.player.Player;

import java.util.List;

/**
 * Controller del caso d'uso "scegli la posizione nel tracciato di risveglio" (UC01).
 */
public class OrderController {

    private final AlchGame alchGame;
    private final Board board;

    public OrderController(AlchGame alchGame, Board board) {
        this.alchGame = alchGame;
        this.board = board;
    }

    public List<String> getAvailableSlots() {
        return board.getAvailableSlotIds();
    }

    public Resources chooseSlot(String orderSlotId) {
        board.assignOrderSlot(orderSlotId, getCurrentPlayer());
        return board.assignSlotResources(orderSlotId, getCurrentPlayer());
    }

    public Player getCurrentPlayer() {
        return alchGame.getCurrentRound().orderPhase().getCurrentPlayer();
    }
}
