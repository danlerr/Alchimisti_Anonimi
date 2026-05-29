package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.board.Resources;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fase di scelta della posizione nel tracciato di risveglio.
 * Ordine: giocatori normali prima (rotazione dal giocatore iniziale), paralizzati in coda.
 * In next(): rimuove la paralisi da tutti prima di creare DeclarationPhase.
 */
public final class OrderPhase implements Phase {

    private final Board board;
    private final List<Player> order;
    private int cursor = 0;

    public OrderPhase(Board board, List<Player> players, int startingPlayerIndex) {
        this.board = board;
        this.order = List.copyOf(buildOrder(players, startingPlayerIndex));
    }

    @Override
    public boolean isComplete() { return cursor >= order.size(); }

    @Override
    public Player getCurrentPlayer() { return order.get(cursor); }

    @Override
    public void advanceTurn() { cursor++; }

    @Override
    public Optional<Phase> next() {
        order.forEach(Player::clearParalysis);
        return Optional.of(new DeclarationPhase(board));
    }

    public List<Player> getTurnOrder() { return order; }

    public List<String> getAvailableSlotIds() { return board.getAvailableSlotIds(); }

    public Resources chooseSlot(String orderSlotId) {
        board.assignOrderSlot(orderSlotId, getCurrentPlayer());
        return board.assignSlotResources(orderSlotId, getCurrentPlayer());
    }

    private List<Player> buildOrder(List<Player> players, int startingPlayerIndex) {
        List<Player> normal = new ArrayList<>();
        List<Player> paralyzed = new ArrayList<>();
        int n = players.size();
        for (int i = 0; i < n; i++) {
            Player p = players.get((startingPlayerIndex + i) % n);
            if (p.isParalyzed()) paralyzed.add(p);
            else                 normal.add(p);
        }
        normal.addAll(paralyzed);
        return normal;
    }
}
