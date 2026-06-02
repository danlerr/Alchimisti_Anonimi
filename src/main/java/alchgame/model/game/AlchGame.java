package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entità Radice (Aggregate Root) che rappresenta l'intera partita.
 * Gestisce il ciclo di vita dei Round e funge da punto di accesso (Facade)
 * per il tabellone, i giocatori e i bersagli.
 */
public class AlchGame {

    // --- Configurazioni immutabili della partita ---
    private final Board board;
    private final int startingActionCubes;
    private final int totalRounds;
    private final Map<String, Target> staticTargets;
    private final String selfId;

    // --- Stato dinamico della partita ---
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
        this.currentRound = new Round(board, List.copyOf(players), startingPlayerIndex);
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
        currentRound = new Round(board, List.copyOf(players), startingPlayerIndex);
    }

    public Map<String, Target> getTargets() {
        Map<String, Target> availableTargets = new LinkedHashMap<>();
        
        if (hasStarted() && !getCurrentRound().isRoundOver()) {
            Player currentPlayer = getCurrentRound().getCurrentPlayer();
            if (currentPlayer != null) {
                availableTargets.put(selfId, currentPlayer);
            }
        }
        availableTargets.putAll(staticTargets);
        return Collections.unmodifiableMap(availableTargets);
    }

    public Target getTarget(String targetId) {
        Target target = getTargets().get(targetId);
        if (target == null) {
            throw new IllegalArgumentException("Target non valido: " + targetId);
        }
        return target;
    }

    // // ------------------------------------------------------------------------
    // // --- 2. ACCESSO ALLO STATO E QUERY (Per i Presenter / View / UI)
    // // ------------------------------------------------------------------------

    public boolean hasStarted() { 
        return currentRound != null; 
    }

    public boolean isOver() { 
        return hasStarted() && currentRoundNumber >= totalRounds && currentRound.isRoundOver(); 
    }

    // public int getCurrentRoundNumber() { return currentRoundNumber; }
    // public int getTotalRounds() { return totalRounds; }
    
    // public Board getBoard() { return board; }
    // public List<Player> getPlayers() { return List.copyOf(players); }

    public Round getCurrentRound() {
        if (currentRound == null) {
            throw new IllegalStateException("Partita non ancora avviata.");
        }
        return currentRound;
    }    
}