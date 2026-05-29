package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Fase di dichiarazione delle azioni.
 * Ordine: inverso della wake-up order (letta dal tabellone al momento della creazione,
 * quindi già aggiornata dai slot scelti in OrderPhase).
 */
public final class DeclarationPhase implements Phase {

    private final Board board;
    private final List<Player> order;
    private int cursor = 0;

    public DeclarationPhase(Board board) {
        this.board = board;
        List<Player> wakeUp = new ArrayList<>(board.getWakeUpOrder());
        Collections.reverse(wakeUp);
        this.order = List.copyOf(wakeUp);
    }

    @Override
    public boolean isComplete() { return cursor >= order.size(); }

    @Override
    public Player getCurrentPlayer() { return order.get(cursor); }

    @Override
    public void advanceTurn() { cursor++; }

    @Override
    public Optional<Phase> next() {
        return Optional.of(new ResolutionPhase(board));
    }

    public List<Player> getTurnOrder() { return order; }

    public void declareAction(String actionSpaceId) {
        board.placeActionCube(actionSpaceId, getCurrentPlayer());
    }
}
