package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlchGame {

    private final Board board;
    private final int startingActionCubes;
    private final int startingGold;
    private final int startingReputation;
    private final int startingIngredients;
    private final int totalRounds;
    private final Map<String, Target> staticTargets;
    private final String selfId;
    private final int minPlayers;
    private final int maxPlayers;
    private final List<Player> players = new ArrayList<>();
    private int currentRoundNumber;
    private int startingPlayerIndex;
    private Round currentRound;

    public AlchGame(Board board,
                    int startingActionCubes,
                    int startingGold,
                    int startingReputation,
                    int startingIngredients,
                    int totalRounds,
                    Map<String, Target> staticTargets,
                    String selfId,
                    int minPlayers,
                    int maxPlayers) {
        this.board = board;
        this.startingActionCubes = startingActionCubes;
        this.startingGold = startingGold;
        this.startingReputation = startingReputation;
        this.startingIngredients = startingIngredients;
        this.totalRounds = totalRounds;
        this.staticTargets = Map.copyOf(staticTargets);
        this.selfId = selfId;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    // --- setup ---

    public void validatePlayerNumber(int n) {
        if (n < minPlayers || n > maxPlayers)
            throw new IllegalArgumentException(
                "Numero giocatori non valido: " + n +
                " (consentito " + minPlayers + "-" + maxPlayers + ").");
    }

    public void validatePlayerName(String name, List<String> currentNames) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome giocatore vuoto.");
        if (currentNames.contains(name))
            throw new IllegalArgumentException("Nome già usato: " + name);
    }

    public void start(List<Player> initialPlayers, int startingPlayerIndex) {
        if (hasStarted())
            throw new IllegalStateException("La partita è già iniziata.");
        validatePlayerList(initialPlayers);

        this.players.addAll(initialPlayers);
        this.currentRoundNumber = 1;
        this.startingPlayerIndex = startingPlayerIndex;
        this.currentRound = new Round(board, List.copyOf(players),
                                      startingPlayerIndex, staticTargets, selfId);
    }

    // --- avanzamento ---

    public void nextRound() {
        if (!hasStarted())
            throw new IllegalStateException("La partita non è ancora iniziata.");
        if (isOver())
            throw new IllegalStateException("La partita è già terminata.");

        for (Player player : players) {
            player.restoreActionCubes(startingActionCubes);
            int favors = player.consumePendingFavors();
            board.dealFavors(player, favors);
        }
        board.resetBoard();
        staticTargets.values().forEach(Target::reset);
        currentRoundNumber++;
        startingPlayerIndex = (startingPlayerIndex + 1) % players.size();
        currentRound = new Round(board, List.copyOf(players),
                                 startingPlayerIndex, staticTargets, selfId);
    }

    // --- query ---

    public boolean isOver() {
        return hasStarted()
            && currentRoundNumber >= totalRounds
            && currentRound.getCurrentPhase() == null;
    }

    public boolean hasStarted()        { return currentRound != null; }
    public int getMinPlayers()         { return minPlayers; }
    public int getMaxPlayers()         { return maxPlayers; }
    public int getStartingGold()       { return startingGold; }
    public int getStartingReputation() { return startingReputation; }
    public int getStartingActionCubes(){ return startingActionCubes; }
    public int getStartingIngredients(){ return startingIngredients; }
    public Board getBoard()            { return board; }

    public Round getCurrentRound() {
        if (!hasStarted())
            throw new IllegalStateException("Partita non ancora avviata.");
        return currentRound;
    }

    // --- support methods ---

    private void validatePlayerList(List<Player> players) {
        if (players == null || players.isEmpty())
            throw new IllegalArgumentException("Lista giocatori vuota.");
        if (players.size() < minPlayers || players.size() > maxPlayers)
            throw new IllegalArgumentException(
                "Numero giocatori non valido: " + players.size() +
                " (consentito " + minPlayers + "-" + maxPlayers + ").");
        long distinct = players.stream().map(Player::getName).distinct().count();
        if (distinct != players.size())
            throw new IllegalArgumentException("Nomi giocatori duplicati.");
    }
// --- target management ---
    public Map<String, Target> getTargets() {
        Map<String, Target> availableTargets = new LinkedHashMap<>();
        Player currentPlayer = getCurrentRound().getCurrentPlayer();
        if (currentPlayer != null) {
            availableTargets.put(selfId, currentPlayer);
        }
        availableTargets.putAll(staticTargets);
        
        return Collections.unmodifiableMap(availableTargets);
    }
    //ritorna il target dato un id
    public Target getTarget(String targetId) {
        Target target = getTargets().get(targetId);
        if (target == null) {
            throw new IllegalArgumentException("Target non valido: " + targetId);
        }
        return target;
    }
}