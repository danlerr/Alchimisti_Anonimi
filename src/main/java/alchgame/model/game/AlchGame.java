package alchgame.model.game;

import alchgame.model.alchemy.AlchemicMapping;
import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.board.Board;
import alchgame.model.player.DeductionGrid;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AlchGame {

    private final Board board;
    private final AlchemicMapping alchemicMapping;
    private Round currentRound;
    private final List<Player> players = new ArrayList<>();
    
    private final int startingActionCubes;
    private final int startingGold;
    private final int startingReputation;
    private final int startingIngredients;
    private final int totalRounds;
    private final int minPlayers;
    private final int maxPlayers;
    private int currentRoundNumber;
    private int startingPlayerIndex;

    private final Map<String, Target> staticTargets;
    private final String selfId;

    public AlchGame(Board board,
                    AlchemicMapping alchemicMapping,

                    int startingActionCubes,
                    int startingGold,
                    int startingReputation,
                    int startingIngredients,
                    int totalRounds,
                    int minPlayers,
                    int maxPlayers,
                    
                    Map<String, Target> staticTargets,
                    String selfId
                    ) {
        this.board = board;
        this.alchemicMapping = alchemicMapping;
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
    /*
     * ritorna il target dato un id
     */
    public Target getTarget(String targetId) {
        Target target = getTargets().get(targetId);
        if (target == null) {
            throw new IllegalArgumentException("Target non valido: " + targetId);
        }
        return target;
    }

    public List<Player> calculateFinalScores() {
        // Un record temporaneo per tenere traccia dei punti durante il calcolo
        record FinalScore(Player player, int correctDeductions) {}
        
        List<FinalScore> scores = new ArrayList<>();

        // 1. Calcolo del punteggio per ogni giocatore
        for (Player p : players) {
            int correctCount = 0;
            DeductionGrid grid = p.getDeductionGrid();

            // Scorre tutti gli ingredienti nella griglia
            for (Ingredient ing : grid.getIngredients()) {
                Optional<AlchemicFormula> playerDeduction = grid.getDeducedFormula(ing);
                
                // Se il giocatore ha isolato esattamente un alchemico per questo ingrediente...
                if (playerDeduction.isPresent()) {
                    
                    // ...lo confrontiamo con la VERA soluzione dell'algoritmo 
                    AlchemicFormula trueFormula = alchemicMapping.getFormulaByIngredient(ing);
                    
                    if (playerDeduction.get().equals(trueFormula)) {
                        correctCount++; // Deduzione corretta!
                    }
                }
            }
            
            scores.add(new FinalScore(p, correctCount));
        }

        // 2. Ordinamento della classifica (Il vincitore sarà all'indice 0)
        scores.sort((s1, s2) -> {
            // Criterio Primario: Numero di deduzioni corrette (ordine decrescente)
            int cmp = Integer.compare(s2.correctDeductions(), s1.correctDeductions());
            
            if (cmp != 0) {
                return cmp;
            }
            
            // Criterio Secondario (Pareggio): Punti Reputazione (ordine decrescente)
            return Integer.compare(s2.player().getReputation(), s1.player().getReputation());
        });
        
        // 3. Restituiamo la lista di giocatori ordinata dal 1° all'ultimo
        return scores.stream().map(FinalScore::player).toList();
    }

        
    
}