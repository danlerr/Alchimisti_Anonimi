package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate root della partita: mantiene lo stato globale e crea i round.
 */
public class AlchGame {

    private final Board board;
    private final int startingActionCubes;
    private final int totalRounds;
    private final List<Player> players = new ArrayList<>();
    private int currentRoundNumber;
    private Round currentRound;
    private int startingPlayerIndex;
    
    public AlchGame(Board board,
                    int startingActionCubes,
                    int totalRounds) {
        this.board = board;
        this.startingActionCubes = startingActionCubes;
        this.totalRounds = totalRounds;
    }

    public void start(List<Player> initialPlayers, int startingPlayerIndex) {
        players.clear();
        players.addAll(initialPlayers);
        this.currentRoundNumber = 1;
        this.startingPlayerIndex = startingPlayerIndex;
        this.currentRound = createRound();
    }

    public void advanceRound() {
        if (currentRoundNumber >= totalRounds)
            throw new IllegalStateException("Non è possibile avanzare: siamo all'ultimo round.");
        for (Player player : players) {
            player.restoreActionCubes(startingActionCubes);
            int favors = player.consumePendingFavors();
            board.dealFavors(player, favors);
        }
        board.resetBoard();
        currentRoundNumber++;
        startingPlayerIndex = (startingPlayerIndex + 1) % players.size();
        currentRound = createRound();
    }

    public Board getBoard() { return board; }

    public List<Player> getPlayers() { return List.copyOf(players); }

    public int getCurrentRoundNumber() { return currentRoundNumber; }

    public int getTotalRounds() { return totalRounds; }

    public Round getCurrentRound() {
        if (currentRound == null)
            throw new IllegalStateException("Partita non ancora avviata.");
        return currentRound;
    }

    private Round createRound() {
        return new Round(board, List.copyOf(players), startingPlayerIndex);
    }
}
