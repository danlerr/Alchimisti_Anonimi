package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.game.phase.DeclarationPhase;
import alchgame.model.game.phase.OrderPhase;
import alchgame.model.game.phase.ResolutionPhase;
import alchgame.model.game.phase.RoundPhase;
import alchgame.model.player.Player;

import java.util.List;

/**
 * Rappresenta il round corrente e governa le sue fasi.
 */
public class Round {

    private final Board board;
    private final List<Player> players;
    private int currentPlayerIndex;

    private final OrderPhase       orderPhase;
    private final DeclarationPhase declarationPhase;
    private final ResolutionPhase  resolutionPhase;
    private RoundPhase currentPhase = RoundPhase.ORDER;

    Round(Board board, List<Player> players, int startingPlayerIndex, List<String> resolutionOrder) {
        this.board = board;
        this.players = List.copyOf(players);
        this.currentPlayerIndex = startingPlayerIndex;
        this.orderPhase       = new OrderPhase(board, startingPlayerIndex);
        this.declarationPhase = new DeclarationPhase(board, startingPlayerIndex);
        this.resolutionPhase  = new ResolutionPhase(board, startingPlayerIndex, resolutionOrder);
    }

    public RoundPhase getCurrentPhase() {
        return currentPhase;
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

    public OrderPhase orderPhase() {
        requirePhase(RoundPhase.ORDER);
        return orderPhase;
    }

    public DeclarationPhase declarationPhase() {
        requirePhase(RoundPhase.DECLARATION);
        return declarationPhase;
    }

    public ResolutionPhase resolutionPhase() {
        requirePhase(RoundPhase.RESOLUTION);
        return resolutionPhase;
    }

    public void advancePhase() {
        currentPhase = switch (currentPhase) {
            case ORDER -> {
                orderPhase.onExit(players);
                yield RoundPhase.DECLARATION;
            }
            case DECLARATION -> RoundPhase.RESOLUTION;
            case RESOLUTION  -> throw new IllegalStateException("RESOLUTION è l'ultima fase del turno.");
        };
    }

    private void requirePhase(RoundPhase expected) {
        if (currentPhase != expected)
            throw new IllegalStateException("Operazione ammessa solo durante " + expected + ", fase corrente: " + currentPhase + ".");
    }
}
