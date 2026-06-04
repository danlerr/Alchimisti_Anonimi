package alchgame.application;

import alchgame.model.board.Board;
import alchgame.model.board.Resources;
import alchgame.model.game.Round;
import alchgame.model.player.Player;
import alchgame.application.observer.*;

import java.util.List;
import java.util.function.Supplier;

/**
 * Controller del caso d'uso "scegli la posizione nel tracciato di risveglio" (UC01).
 */
public class OrderController extends Subject<ActionObserver> {

    private final Supplier<Round> round;;
    private final Board board;

    public OrderController(Supplier<Round> round, Board board) {
        this.round = round;
        this.board = board;
    }

    public List<String> getAvailableSlots() {
        return board.getAvailableSlotIds();
    }

    public Resources chooseSlot(String orderSlotId) {
        board.assignOrderSlot(orderSlotId, getCurrentPlayer());
        notifyObservers(ActionObserver::onActionCompleted);
        return board.assignSlotResources(orderSlotId, getCurrentPlayer());
    }

    public Player getCurrentPlayer() {
        return round.get().getCurrentPlayer();
    }
}
