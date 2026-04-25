package alchgame.service;

import alchgame.GameConfig;
import alchgame.model.AlchemicFormula;
import alchgame.model.Board;
import alchgame.model.DeductionGrid;
import alchgame.model.Ingredient;
import alchgame.model.Player;
import alchgame.model.PrivateLaboratory;
import alchgame.model.PublicPlayerBoard;
import alchgame.model.ResultsTriangle;
import alchgame.model.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AlchGame — rappresenta una singola partita in corso.
 * Centralizza lo stato di gioco (giocatori, tabellone, dati statici di partita)
 * e il suo ciclo di vita (SETUP → PLAYING → ENDED).
 */
public class AlchGame {

    public enum Lifecycle { SETUP, PLAYING, ENDED }

    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;

    private final Board board;
    private final List<Ingredient> ingredients;
    private final List<AlchemicFormula> formulas;
    private final AlchemicMapping alchemicMapping;
    private final Map<String, Target> externalTargets;
    private final String selfTargetId;

    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIndex;
    private int currentRound = 0;
    private int startingPlayerIndex = 0;
    private Lifecycle lifecycle = Lifecycle.SETUP;

    public AlchGame(Board board,
                    List<Ingredient> ingredients,
                    List<AlchemicFormula> formulas,
                    AlchemicMapping alchemicMapping,
                    Map<String, Target> externalTargets,
                    String selfTargetId) {
        this.board = board;
        this.ingredients = List.copyOf(ingredients);
        this.formulas = List.copyOf(formulas);
        this.alchemicMapping = alchemicMapping;
        this.externalTargets = new HashMap<>(externalTargets);
        this.selfTargetId = selfTargetId;
    }

    // ---- setup --------------------------------------------------------------

    public void initializePlayers(List<String> names) {
        if (lifecycle != Lifecycle.SETUP)
            throw new IllegalStateException("Giocatori inizializzabili solo in fase SETUP.");
        if (names == null || names.isEmpty())
            throw new IllegalArgumentException("Lista giocatori vuota.");
        if (names.size() < MIN_PLAYERS || names.size() > MAX_PLAYERS)
            throw new IllegalArgumentException(
                "Numero giocatori non valido: " + names.size() +
                " (consentito " + MIN_PLAYERS + "–" + MAX_PLAYERS + ").");
        long distinct = names.stream().distinct().count();
        if (distinct != names.size())
            throw new IllegalArgumentException("Nomi giocatori duplicati.");

        for (String name : names) {
            Player p = createPlayer(name);
            players.add(p);
            board.dealIngredients(p, GameConfig.STARTING_INGREDIENTS);
        }

        this.currentPlayerIndex = 0;
        this.currentRound = 1;
        this.startingPlayerIndex = new java.util.Random().nextInt(names.size());
        this.lifecycle = Lifecycle.PLAYING;
    }

    private Player createPlayer(String name) {
        DeductionGrid grid = new DeductionGrid(ingredients, formulas);
        PrivateLaboratory lab = new PrivateLaboratory(new ArrayList<>(), grid, new ResultsTriangle());
        return new Player(name, GameConfig.STARTING_GOLD, GameConfig.STARTING_REPUTATION,
                          lab, new PublicPlayerBoard());
    }

    // ---- dati statici di partita -------------------------------------------

    public Board getBoard() { return board; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<AlchemicFormula> getFormulas() { return formulas; }
    public AlchemicMapping getAlchemicMapping() { return alchemicMapping; }

    // ---- player corrente ----------------------------------------------------

    public List<Player> getPlayers() { return List.copyOf(players); }

    public Player getCurrentPlayer() {
        if (lifecycle == Lifecycle.SETUP)
            throw new IllegalStateException("Partita non ancora avviata.");
        return players.get(currentPlayerIndex);
    }

    public void setCurrentPlayer(Player player) {
        int idx = players.indexOf(player);
        if (idx < 0)
            throw new IllegalArgumentException("Player non in partita.");
        currentPlayerIndex = idx;
    }

    // ---- ordinamento fasi ---------------------------------------------------

    /** Ordine fase ordine: senso orario da startingPlayerIndex. */
    public List<Player> getOrderPhaseOrder() {
        List<Player> order = new ArrayList<>();
        int n = players.size();
        for (int i = 0; i < n; i++)
            order.add(players.get((startingPlayerIndex + i) % n));
        return order;
    }

    /** Ordine dichiarazione: inverso wake-up (bottom → top). */
    public List<Player> getDeclarationPhaseOrder() {
        List<Player> wakeUp = new ArrayList<>(board.getWakeUpOrder());
        Collections.reverse(wakeUp);
        return wakeUp;
    }

    /**
     * Ordine risoluzione per uno spazio azione: wake-up (top → bottom),
     * filtrato ai soli player che hanno dichiarato quell'azione.
     */
    public List<Player> getResolutionOrderFor(String actionSpaceId) {
        List<Player> declared = board.getActionSpace(actionSpaceId).getDeclaredPlayers();
        List<Player> resolutionOrder = new ArrayList<>();
        for (Player player : board.getWakeUpOrder()) {
            for (Player declaredPlayer : declared) {
                if (declaredPlayer.equals(player))
                    resolutionOrder.add(player);
            }
        }
        return resolutionOrder;
    }

    // ---- round lifecycle ----------------------------------------------------

    public int getCurrentRound() { return currentRound; }
    public int getTotalRounds()  { return GameConfig.TOTAL_ROUNDS; }

    public void endRound() {
        if (lifecycle != Lifecycle.PLAYING)
            throw new IllegalStateException("endRound chiamabile solo durante PLAYING.");
        players.forEach(Player::restoreActionCubes);
        board.resetRound();
        if (currentRound >= GameConfig.TOTAL_ROUNDS) {
            lifecycle = Lifecycle.ENDED;
            return;
        }
        currentRound++;
        startingPlayerIndex = (startingPlayerIndex + 1) % players.size();
        currentPlayerIndex = startingPlayerIndex;
    }

    // ---- targets ------------------------------------------------------------

    public Target getTarget(String targetId) {
        if (selfTargetId != null && selfTargetId.equals(targetId))
            return getCurrentPlayer();
        Target t = externalTargets.get(targetId);
        if (t == null)
            throw new IllegalArgumentException("Target non trovato: " + targetId);
        return t;
    }

    // ---- lifecycle ----------------------------------------------------------

    public Lifecycle getLifecycle() { return lifecycle; }
    public boolean isStarted() { return lifecycle == Lifecycle.PLAYING; }

    public void end() {
        if (lifecycle != Lifecycle.PLAYING)
            throw new IllegalStateException("Fine partita ammessa solo durante PLAYING.");
        lifecycle = Lifecycle.ENDED;
    }
}
