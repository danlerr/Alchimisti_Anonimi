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
    //private final Map<String, Target> externalTargets;
    private final String selfId;

    private final int startingActionCubes;
    private final int totalRounds;

    private final List<Player> players = new ArrayList<>();
    private GameStatus gameStatus = GameStatus.SETUP;

    private int startingPlayerIndex;
    private Round round;

    public AlchGame(Board board,
                    Map<String, Target> externalTargets,
                    String selfId,
                    int startingActionCubes,
                    int totalRounds) {
        this.board = board;
        //this.externalTargets = new HashMap<>(externalTargets);
        this.selfId = selfId;
        this.startingActionCubes = startingActionCubes;
        this.totalRounds = totalRounds;
    }

    //primo round -> da mettere nel boots
    public void start(List<Player> initialPlayers, int startingPlayerIndex) {
        if (gameStatus != GameStatus.SETUP)
            throw new IllegalStateException("Partita avviabile solo in fase SETUP.");
        if (initialPlayers == null || initialPlayers.isEmpty())
            throw new IllegalArgumentException("Lista giocatori vuota.");
        if (startingPlayerIndex < 0 || startingPlayerIndex >= initialPlayers.size())
            throw new IllegalArgumentException("Indice primo giocatore non valido.");

        this.players.clear();
        this.players.addAll(initialPlayers);
        this.startingPlayerIndex = startingPlayerIndex;
        this.gameStatus = GameStatus.PLAYING;
        this.round = new Round(board, List.copyOf(players), startingActionCubes, startingPlayerIndex,
                externalTargets, selfId);
    }

    public Board getBoard() { return board; }

    public List<Player> getPlayers() { return List.copyOf(players); }

    public int getTotalRounds()  { return totalRounds; }

    public void startTurn() {
        requirePlaying();

        for (Player player : players) {
            player.restoreActionCubes(startingActionCubes);
            int favors = player.consumePendingFavors();
            board.dealFavors(player, favors);
        }
        board.resetRound();

        currentRound++;
        startingPlayerIndex = (startingPlayerIndex + 1) % players.size();
        //turn.resetTurn(startingPlayerIndex);
        newTurn()????
    }

    public Turn getTurn() {
        if (turn == null)
            throw new IllegalStateException("Partita non ancora avviata.");
        return turn;
    }

    // status da togliere

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
