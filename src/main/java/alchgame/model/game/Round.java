package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.board.Resources;
import alchgame.model.game.phase.OrderState;
import alchgame.model.game.phase.RoundState;
import alchgame.model.player.Player;

import java.util.List;

/**
 * Rappresenta il round corrente e governa le sue fasi.
 */
public class Round {

    private final Board board;
    private final List<Player> players;
    private final int startingPlayerIndex;
    private int currentPlayerIndex;
    private RoundState currentPhase;

    Round(Board board, List<Player> players, int startingPlayerIndex) {
        this.board = board;
        this.players = List.copyOf(players);
        this.startingPlayerIndex = startingPlayerIndex;
        this.currentPlayerIndex = startingPlayerIndex;
        this.currentPhase = new OrderState();
    }

    public RoundPhase getCurrentPhase() {
        return currentPhase.getPhase();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void setCurrentPlayer(Player player) {
        int idx = players.indexOf(player);
        currentPlayerIndex = idx;
    }

    public void advancePhase() {
        currentPhase = currentPhase.advance(players, board);
    }

    public Resources chooseSlot(String orderSlotID) {
        requirePhase(RoundPhase.ORDER);
        board.assignOrderSlot(orderSlotID, getCurrentPlayer());
        return board.assignSlotResources(orderSlotID, getCurrentPlayer());
    }

    public void declareAction(String actionSpaceId) {
        requirePhase(RoundPhase.DECLARATION);
        board.placeActionCube(actionSpaceId, getCurrentPlayer());
    }

    public List<String> getAvailableSlotIds() {
        return board.getAvailableSlotIds();
    }

    private void requirePhase(RoundPhase expected) {
        if (currentPhase.getPhase() != expected)
            throw new IllegalStateException("Operazione ammessa solo durante " + expected + ", fase corrente: " + currentPhase.getPhase() + ".");
    }


    // GET ORDINI FASI

    public List<Player> getOrderPhaseOrder() {
        requirePhase(RoundPhase.ORDER);
        return currentPhase.getPhaseOrder(players, startingPlayerIndex, board);
    }

    public List<Player> getDeclarationPhaseOrder() {
        requirePhase(RoundPhase.DECLARATION);
        return currentPhase.getPhaseOrder(players, startingPlayerIndex, board);
    }

    public List<Player> getResolutionOrder(String actionSpaceId) {
        requirePhase(RoundPhase.RESOLUTION);
         return currentPhase.getResolutionOrder(board, actionSpaceId);
    }
}
