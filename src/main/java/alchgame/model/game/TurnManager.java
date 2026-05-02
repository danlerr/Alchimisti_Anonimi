package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.board.Resources;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TurnManager {

    private final Board board;
    private final List<Player> players;
    private final Map<String, Target> externalTargets;
    private final String selfTargetId;

    private int startingPlayerIndex;
    private int currentPlayerIndex;
    private TurnPhase currentPhase;

    TurnManager(Board board, List<Player> players, int startingActionCubes, int startingPlayerIndex,
                Map<String, Target> externalTargets, String selfTargetId) {
        this.board = board;
        this.players = players;
        this.startingPlayerIndex = startingPlayerIndex;
        this.currentPlayerIndex = startingPlayerIndex;
        this.currentPhase = TurnPhase.ORDER;
        this.externalTargets = externalTargets;
        this.selfTargetId = selfTargetId;
    }

    public void resetTurn(int newStartingPlayerIndex) {
        this.startingPlayerIndex = newStartingPlayerIndex;
        this.currentPlayerIndex  = newStartingPlayerIndex;
        this.currentPhase        = TurnPhase.ORDER;
    }


    // ---- targets ------------------------------------------------------------

    public Target getTarget(String targetId) {
        if (selfTargetId.equals(targetId)) return getCurrentPlayer();
        Target t = externalTargets.get(targetId);
        if (t == null) throw new IllegalArgumentException("Target non trovato: " + targetId);
        return t;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void setCurrentPlayer(Player player) {
        int idx = players.indexOf(player);
        if (idx < 0)
            throw new IllegalArgumentException("Player non in partita.");
        currentPlayerIndex = idx;
    }

    public TurnPhase getCurrentPhase() { return currentPhase; }

    public void advanceTo(TurnPhase target) {
        if (currentPhase == target) return;
        TurnPhase next = currentPhase.next();
        if (next != target)
            throw new IllegalStateException(
                "Transizione non ammessa da " + currentPhase + " a " + target + ".");
        if (currentPhase == TurnPhase.ORDER)
            players.forEach(Player::clearParalysis);
        currentPhase = target;
    }

    // ---- ORDER --------------------------------------------------------------

    public List<Player> getOrderPhaseOrder() {
        requirePhase(TurnPhase.ORDER);
        List<Player> normal    = new ArrayList<>();
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

    public Resources chooseSlot(String orderSlotID) {
        requirePhase(TurnPhase.ORDER);
        return board.assignOrderSlot(orderSlotID, getCurrentPlayer());
    }

    // ---- DECLARATION --------------------------------------------------------

    public List<Player> getDeclarationPhaseOrder() {
        requirePhase(TurnPhase.DECLARATION);
        List<Player> wakeUp = new ArrayList<>(board.getWakeUpOrder());
        Collections.reverse(wakeUp);
        return wakeUp;
    }

    public void declareAction(String actionSpaceId) {
        requirePhase(TurnPhase.DECLARATION);
        board.placeActionCube(actionSpaceId, getCurrentPlayer());
    }

    // ---- RESOLUTION ---------------------------------------------------------

    public List<Player> getResolutionOrderFor(String actionSpaceId) {
        requirePhase(TurnPhase.RESOLUTION);
        List<Player> declared = board.getActionSpace(actionSpaceId).getDeclaredPlayers();
        List<Player> order    = new ArrayList<>();
        for (Player p : board.getWakeUpOrder())
            if (declared.contains(p)) order.add(p);
        return order;
    }

    // ---- helpers for controller layer ---------------------------------------

    public List<String> getAvailableSlotIds() {
        return board.getAvailableSlotIds();
    }

    public List<String> getWakeUpOrderNames() {
        return board.getWakeUpOrder().stream()
                .map(Player::getName)
                .toList();
    }

    public void setCurrentPlayerByName(String name) {
        players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .ifPresentOrElse(
                        this::setCurrentPlayer,
                        () -> { throw new IllegalArgumentException("Giocatore non trovato: " + name); }
                );
    }

    // -------------------------------------------------------------------------

    private void requirePhase(TurnPhase expected) {
        if (currentPhase != expected)
            throw new IllegalStateException(
                "Operazione ammessa solo durante " + expected + ", fase corrente: " + currentPhase + ".");
    }
}
