package alchgame.application;

import alchgame.application.dto.SlotResultDTO;
import alchgame.model.board.Board;
import alchgame.model.board.Favor;
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

    public OrderController(Supplier<Round> round, Board board) {
        this.round = round;
    }

    public List<String> getAvailableSlots() {
        return round.get().getBoard().getAvailableSlotIds();
    }

    public SlotResultDTO chooseSlot(String orderSlotId) {
        Player player = round.get().getCurrentPlayer();
        round.get().getBoard().assignOrderSlot(orderSlotId, player);
        Resources res = round.get().getBoard().assignSlotResources(orderSlotId, player);
        return new SlotResultDTO(res.ingredientCount(), res.favorCount());
    }

    public void endOrder() {
        notifyObservers(ActionObserver::onActionPerformed);
    }

    public List<String> getPlayerFavors() {
        return round.get().getCurrentPlayer().getFavorCards().stream()
                .map(Favor::getName)
                .toList();
    }

    public void activateFavor(String favorName) {
        Player player = round.get().getCurrentPlayer();
        Board board = round.get().getBoard();
        Favor favor = player.getFavorCards().stream()
                .filter(f -> f.getName().equals(favorName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Carta non trovata: " + favorName));
        player.useFavor(favor, board);
    }
}
