package alchgame.model.game;

import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.board.Board;
import alchgame.model.board.Resources;


import alchgame.model.alchemy.Ingredient;
import alchgame.model.player.Player;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GameSession — rappresenta una singola partita in corso.
 * Centralizza lo stato di gioco (giocatori, tabellone, dati statici di partita)
 * e il suo ciclo di vita (SETUP → PLAYING → ENDED).
 */
public class GameSession {

    private final Board board;
    private final List<Ingredient> ingredients;
    private final List<AlchemicFormula> formulas;
    private final Map<String, Target> externalTargets;
    private final String selfTargetId;

    private final int startingActionCubes;
    private final int totalRounds;

    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIndex;
    private int currentRound = 0;
    private int startingPlayerIndex = 0;
    private GameStatus gameStatus = GameStatus.SETUP;
    private TurnPhase currentPhase;

    public GameSession(Board board,
                    List<Ingredient> ingredients,
                    List<AlchemicFormula> formulas,
                    Map<String, Target> externalTargets,
                    String selfTargetId,
                    int startingActionCubes,
                    int totalRounds) {
        this.board = board;
        this.ingredients = List.copyOf(ingredients);
        this.formulas = List.copyOf(formulas);
        this.externalTargets = new HashMap<>(externalTargets);
        this.selfTargetId = selfTargetId;
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
        this.currentPlayerIndex = startingPlayerIndex;
        this.currentRound = 1;
        this.startingPlayerIndex = startingPlayerIndex;
        this.gameStatus = GameStatus.PLAYING;
        this.currentPhase = TurnPhases.order();
    }

    // ---- dati statici di partita -------------------------------------------

    public Board getBoard() { return board; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<AlchemicFormula> getFormulas() { return formulas; }

    // ---- player corrente ----------------------------------------------------

    public List<Player> getPlayers() { return List.copyOf(players); }

    public Player getCurrentPlayer() {
        if (gameStatus == GameStatus.SETUP)
            throw new IllegalStateException("Partita non ancora avviata.");
        return players.get(currentPlayerIndex);
    }

    public void setCurrentPlayer(Player player) {
        int idx = players.indexOf(player);
        if (idx < 0)
            throw new IllegalArgumentException("Player non in partita.");
        currentPlayerIndex = idx;
    }

    // ---- round lifecycle ----------------------------------------------------

    public int getCurrentRound() { return currentRound; }
    public int getTotalRounds()  { return totalRounds; }
    public TurnPhaseType getCurrentPhase() {
        return currentPhase == null ? null : currentPhase.type();
    }

    public void advanceTo(TurnPhaseType targetPhase) {
        requirePlaying();
        if (currentPhase.type() == targetPhase)
            return;

        TurnPhase nextPhase = currentPhase.next();
        if (nextPhase.type() != targetPhase) {
            throw new IllegalStateException(
                "Transizione non ammessa da " + currentPhase.type() + " a " + targetPhase + "."
            );
        }
        currentPhase = nextPhase;
    }

    public List<Player> getOrderPhaseOrder() {
        requirePlaying();
        return currentPhase.getOrderPhaseOrder(this);
    }

    public Resources chooseSlot(String orderSlotID) {
        requirePlaying();
        return currentPhase.chooseSlot(this, orderSlotID);
    }

    public List<Player> getDeclarationPhaseOrder() {
        requirePlaying();
        return currentPhase.getDeclarationPhaseOrder(this);
    }

    public void declareAction(String actionSpaceId) {
        requirePlaying();
        currentPhase.declareAction(this, actionSpaceId);
    }

    public List<Player> getResolutionOrderFor(String actionSpaceId) {
        requirePlaying();
        return currentPhase.getResolutionOrderFor(this, actionSpaceId);
    }

    public void endRound() {
        requirePlaying();
        currentPhase.endRound(this);
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

    public GameStatus getLifecycle() { return gameStatus; }
    public boolean isStarted() { return gameStatus == GameStatus.PLAYING; }

    public void end() {
        requirePlaying();
        finish();
    }

    public int getStartingActionCubes() { return startingActionCubes; }
    public int getStartingPlayerIndex() { return startingPlayerIndex; }

    void startNextRound(int nextRound, int nextStartingPlayerIndex) {
        this.currentRound = nextRound;
        this.startingPlayerIndex = nextStartingPlayerIndex;
        this.currentPlayerIndex = nextStartingPlayerIndex;
        this.currentPhase = TurnPhases.order();
    }

    void finish() {
        gameStatus = GameStatus.ENDED;
        currentPhase = null;
    }

    private void requirePlaying() {
        if (gameStatus != GameStatus.PLAYING)
            throw new IllegalStateException("Operazione ammessa solo durante PLAYING.");
    }
}
