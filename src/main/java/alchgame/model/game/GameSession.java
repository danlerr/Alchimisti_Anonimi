package alchgame.model.game;

import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.board.Board;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GameSession — aggregate root della partita.
 * Possiede configurazione statica, roster giocatori, lifecycle e contatore dei round.
 * Il flusso interno di un turno è delegato a {@link TurnManager}, ricreato a ogni round.
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
    private GameStatus gameStatus = GameStatus.SETUP;

    private int currentRound;
    private int startingPlayerIndex;
    private TurnManager turnManager;

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
        this.currentRound = 1;
        this.startingPlayerIndex = startingPlayerIndex;
        this.gameStatus = GameStatus.PLAYING;
        this.turnManager = new TurnManager(board, List.copyOf(players), startingActionCubes, startingPlayerIndex,
                externalTargets, selfTargetId);
    }

    // ---- dati statici -------------------------------------------------------

    public Board getBoard() { return board; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<AlchemicFormula> getFormulas() { return formulas; }
    public int getStartingActionCubes() { return startingActionCubes; }

    // ---- roster -------------------------------------------------------------

    public List<Player> getPlayers() { return List.copyOf(players); }

    // ---- round --------------------------------------------------------------

    public int getCurrentRound() { return currentRound; }
    public int getTotalRounds()  { return totalRounds; }

    /**
     * Esegue il cleanup di fine turno e avanza al round successivo,
     * oppure termina la partita se i round sono esauriti.
     */
    public void advanceRound() {
        requirePlaying();

        for (Player player : players) {
            player.restoreActionCubes(startingActionCubes);
            int favors = player.consumePendingFavors();
            board.dealFavors(player, favors);
        }
        board.resetRound();

        if (currentRound >= totalRounds) {
            finish();
            return;
        }

        currentRound++;
        startingPlayerIndex = (startingPlayerIndex + 1) % players.size();
        turnManager.resetTurn(startingPlayerIndex);
    }

    // ---- turn manager -------------------------------------------------------

    public TurnManager getTurnManager() {
        if (turnManager == null)
            throw new IllegalStateException("Partita non ancora avviata.");
        return turnManager;
    }

    // ---- targets ------------------------------------------------------------

    public Target getTarget(String targetId) {
        return getTurnManager().getTarget(targetId);
    }

    // ---- lifecycle ----------------------------------------------------------

    public GameStatus getLifecycle() { return gameStatus; }
    public boolean isStarted() { return gameStatus == GameStatus.PLAYING; }

    public void end() {
        requirePlaying();
        finish();
    }

    private void finish() {
        gameStatus = GameStatus.ENDED;
    }

    private void requirePlaying() {
        if (gameStatus != GameStatus.PLAYING)
            throw new IllegalStateException("Operazione ammessa solo durante PLAYING.");
    }
}
