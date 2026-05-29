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
    private final int totalRounds;
    private final Map<String, Target> staticTargets;
    private final String selfId;
    private final List<Player> players = new ArrayList<>();
    private int currentRoundNumber;
    private Round currentRound;
    private int startingPlayerIndex;

    public AlchGame(Board board,
                    int startingActionCubes,
                    int totalRounds,
                    Map<String, Target> staticTargets,
                    String selfId) {
        this.board = board;
        this.startingActionCubes = startingActionCubes;
        this.totalRounds = totalRounds;
        this.staticTargets = Map.copyOf(staticTargets);
        this.selfId = selfId;
    }

    public void start(List<Player> initialPlayers, int startingPlayerIndex) {
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

    public boolean hasStarted() { return currentRound != null; }

    public boolean isOver() { return hasStarted() && currentRoundNumber >= totalRounds; }

    public Round getCurrentRound() {
        if (currentRound == null)
            throw new IllegalStateException("Partita non ancora avviata.");
        return currentRound;
    }

    public Map<String, Target> getTargets() {
        Map<String, Target> all = new LinkedHashMap<>();
        all.put(selfId, getCurrentRound().getCurrentPlayer());
        all.putAll(staticTargets);
        return Collections.unmodifiableMap(all);
    }

    public Target getTarget(String targetId) {
        Target t = getTargets().get(targetId);
        if (t == null) throw new IllegalArgumentException("Target non valido: " + targetId);
        return t;
    }

    private Round createRound() {
        return new Round(board, List.copyOf(players), startingPlayerIndex);
    }
}
