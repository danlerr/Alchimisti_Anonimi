package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GameSession — aggregate root della partita.
 * Possiede configurazione statica, roster giocatori, lifecycle e contatore dei round.
 * Il flusso interno di un turno è delegato a {@link Turn}, ricreato a ogni round.
 */
public class AlchGame {

    private final Board board;
    private final Map<String, Target> externalTargets;
    private final String selfId;

    private final int startingActionCubes;
    private final int totalRounds;

    private final List<Player> players = new ArrayList<>();
    private GameStatus gameStatus = GameStatus.SETUP;

    private int currentRound;
    private int startingPlayerIndex;
    private Turn turn;

    public AlchGame(Board board,
                    Map<String, Target> externalTargets,
                    String selfId,
                    int startingActionCubes,
                    int totalRounds) {
        this.board = board;
        this.externalTargets = new HashMap<>(externalTargets);
        this.selfId = selfId;
        this.startingActionCubes = startingActionCubes;
        this.totalRounds = totalRounds;
    }

    // ---- setup --------------------------------------------------------------

    public void start(List<Player> initialPlayers, int startingPlayerIndex) {
        if (gameStatus != GameStatus.SETUP)
            throw new IllegalStateException("Partita avviabile solo in fase SETUP.");
        if (initialPlayers == null || initialPlayers.isEmpty())
            throw new IllegalArgumentException("Lista giocatori vuota.");
        if (startingPlayerIndex < 0 || startingPlayerIndex >= initialPlayers.size())
            throw new IllegalArgumentException("Indice primo giocatore non valido.");

        this.players.clear();
        this.players.addAll(initialPlayers);
        this.currentRound = 1;
        this.startingPlayerIndex = startingPlayerIndex;
        this.gameStatus = GameStatus.PLAYING;
        this.turn = new Turn(board, List.copyOf(players), startingActionCubes, startingPlayerIndex,
                externalTargets, selfId);
    }

    // ---- board --------------------------------------------------------------

    public Board getBoard() { return board; }

    // ---- players -------------------------------------------------------------

    public List<Player> getPlayers() { return List.copyOf(players); }

    // ---- round --------------------------------------------------------------

    public int getCurrentRound() { return currentRound; }
    public int getTotalRounds()  { return totalRounds; }

    public void advanceRound() {
        requirePlaying();

        for (Player player : players) {
            player.restoreActionCubes(startingActionCubes);
            int favors = player.consumePendingFavors();
            board.dealFavors(player, favors);
        }
        board.resetRound();

        if (currentRound >= totalRounds) {
            gameStatus = GameStatus.ENDED;
            return;
        }

        currentRound++;
        startingPlayerIndex = (startingPlayerIndex + 1) % players.size();
        turn.resetTurn(startingPlayerIndex);
    }

    // ---- turn ---------------------------------------------------------------

    public Turn getTurn() {
        if (turn == null)
            throw new IllegalStateException("Partita non ancora avviata.");
        return turn;
    }

    // ---- status ----------------------------------------------------------

    public GameStatus getStatus() { return gameStatus; }
    public boolean isStarted() { return gameStatus == GameStatus.PLAYING; }

    public void endGame() {
        requirePlaying();
        gameStatus = GameStatus.ENDED;
    }

    private void requirePlaying() {
        if (gameStatus != GameStatus.PLAYING)
            throw new IllegalStateException("Operazione ammessa solo durante PLAYING.");
    }
}
