package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.board.Resources;
import alchgame.model.game.phase.OrderState;
import alchgame.model.game.phase.TurnState;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Turn {

    private final Board board;
    private final List<Player> players;
    private final Map<String, Target> externalTargets;
    private final String selfId;

    private int startingPlayerIndex;
    private int currentPlayerIndex;
    private TurnState currentState;

    Turn(Board board, List<Player> players, int startingActionCubes, int startingPlayerIndex,
         Map<String, Target> externalTargets, String selfId) {
        this.board = board;
        this.players = players;
        this.startingPlayerIndex = startingPlayerIndex;
        this.currentPlayerIndex = startingPlayerIndex;
        this.currentState = new OrderState();
        this.externalTargets = externalTargets;
        this.selfId = selfId;
    }

    public void resetTurn(int newStartingPlayerIndex) {
        this.startingPlayerIndex = newStartingPlayerIndex;
        this.currentPlayerIndex  = newStartingPlayerIndex;
        this.currentState      = new OrderState();
    }

    // ---- phase --------------------------------------------------------------

    public TurnPhase getCurrentPhase() {
        return currentState.getPhase();
    }

    public void advancePhase() {
        currentState = currentState.advance(players, board);
    }

    // ---- targets ------------------------------------------------------------

    public Target getTarget(String targetId) {
        if (selfId.equals(targetId)) return getCurrentPlayer();
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

    // ---- ORDER --------------------------------------------------------------

    public List<Player> getOrderPhaseOrder() {
        requirePhase(TurnPhase.ORDER);
        return currentState.getPhaseOrder(players, startingPlayerIndex, board);
    }

    public Resources chooseSlot(String orderSlotID) {
        requirePhase(TurnPhase.ORDER);
        return board.assignOrderSlot(orderSlotID, getCurrentPlayer());
    }

    // ---- DECLARATION --------------------------------------------------------

    public List<Player> getDeclarationPhaseOrder() {
        requirePhase(TurnPhase.DECLARATION);
        return currentState.getPhaseOrder(players, startingPlayerIndex, board);
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
        if (currentState.getPhase() != expected)
            throw new IllegalStateException(
                "Operazione ammessa solo durante " + expected + ", fase corrente: " + currentState.getPhase() + ".");
    }
}
