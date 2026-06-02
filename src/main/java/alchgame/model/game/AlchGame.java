package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entità Radice (Aggregate Root) della partita.
 * Si occupa SOLO della gestione dei Macro-Cicli (i Round) e della manutenzione 
 * di fine turno. Delega tutte le regole delle fasi al Round corrente.
 */
public class AlchGame {

    private final Board board;
    private final int startingActionCubes;
    private final int totalRounds;
    private final Map<String, Target> staticTargets;
    private final String selfId;
    private final List<Player> players = new ArrayList<>();
    private int currentRoundNumber;
    private int startingPlayerIndex;
    private Round currentRound;

    public AlchGame(Board board, int startingActionCubes, int totalRounds,
                    Map<String, Target> staticTargets, String selfId) {
        this.board = board;
        this.startingActionCubes = startingActionCubes;
        this.totalRounds = totalRounds;
        this.staticTargets = Map.copyOf(staticTargets);
        this.selfId = selfId;
    }

    public void start(List<Player> initialPlayers, int startingPlayerIndex) {
        if (hasStarted()) throw new IllegalStateException("La partita è già iniziata.");
        
        this.players.addAll(initialPlayers);
        this.currentRoundNumber = 1;
        this.startingPlayerIndex = startingPlayerIndex;
        this.currentRound = new Round(board, List.copyOf(players), startingPlayerIndex, staticTargets, selfId);
    }

    public void nextRound() {
        if (isOver()) {
            throw new IllegalStateException("Non è possibile avanzare: la partita è terminata.");
        }
        
        for (Player player : players) {
            player.restoreActionCubes(startingActionCubes);
            int favors = player.consumePendingFavors();
            board.dealFavors(player, favors);
        }
        board.resetBoard();
        staticTargets.values().forEach(Target::reset); 
        currentRoundNumber++;
        startingPlayerIndex = (startingPlayerIndex + 1) % players.size();
        currentRound = new Round(board, List.copyOf(players), startingPlayerIndex, staticTargets, selfId);
    }

    public boolean isOver() { 
        return (hasStarted()) && currentRoundNumber >= totalRounds && currentRound.getCurrentPhase() == null; 
    }

    public Board getBoard() {
        return board;
    }

    public Round getCurrentRound() {
        if (hasStarted()) throw new IllegalStateException("Partita non ancora avviata.");
        return currentRound;
    }    

    public boolean hasStarted() {
        return currentRound != null;
    }
}