package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fase di risoluzione delle azioni.
 * Gli step sono costruiti lazily al primo accesso: per ogni azione (in ordine di
 * resolutionOrder = board.getActionSpaceIds()) si elencano i giocatori che l'hanno
 * dichiarata, ordinati per wake-up order e ripetuti tante volte quante le dichiarazioni.
 * next() restituisce empty: è l'ultima fase del round.
 */
public final class ResolutionPhase implements Phase {

    private record ScheduledAction(String actionId, Player player) {}

    private final Board board;
    private List<ScheduledAction> order = null;
    private int cursor = 0;

    public ResolutionPhase(Board board) {
        this.board = board;
        this.order = List.copyOf(buildOrder());
    }

    @Override
    public boolean isComplete() {
        return cursor >= order.size();
    }

    @Override
    public Player getCurrentPlayer() {
        return order.get(cursor).player();
    }

    @Override
    public void nextPlayer() {
        cursor++;
    }

    @Override
    public Optional<Phase> next() {
        return Optional.empty();
    }

    private List<ScheduledAction> buildOrder() {
        List<Player> wakeUpOrder = board.getWakeUpOrder();
        List<ScheduledAction> result = new ArrayList<>();
        for (String actionId : board.getActionSpaceIds()) {
            List<Player> declared = board.getActionSpace(actionId).getDeclaredPlayers();
            for (Player player : wakeUpOrder) {
                long declarations = declared.stream().filter(player::equals).count();
                for (int i = 0; i < declarations; i++) {
                    result.add(new ScheduledAction(actionId, player));
                }
            }
        }
        return List.copyOf(result);
    }
}
