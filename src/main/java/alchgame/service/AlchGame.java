package alchgame.service;

import alchgame.model.AlchemicFormula;
import alchgame.model.Board;
import alchgame.model.Ingredient;
import alchgame.model.Player;
import alchgame.model.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AlchGame — rappresenta una singola partita in corso.
 * Centralizza lo stato di gioco (giocatori, tabellone, dati statici di partita)
 * e il suo ciclo di vita (SETUP → PLAYING → ENDED).
 *
 * Pure Fabrication / Facade: non contiene logica di dominio (che resta nei
 * domain object), espone solo accesso coeso allo stato della partita.
 */
public class AlchGame {

    public enum Lifecycle { SETUP, PLAYING, ENDED }

    private final Board board;
    private final List<Ingredient> ingredients;
    private final List<AlchemicFormula> formulas;
    private final AlchemicMapping alchemicMapping;
    private final Map<String, Target> externalTargets;
    private final String selfTargetId;

    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIndex;
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

    public void initializePlayers(List<Player> newPlayers) {
        if (lifecycle != Lifecycle.SETUP)
            throw new IllegalStateException("Giocatori inizializzabili solo in fase SETUP.");
        if (newPlayers == null || newPlayers.isEmpty())
            throw new IllegalArgumentException("Lista giocatori vuota.");
        this.players.addAll(newPlayers);
        this.currentPlayerIndex = 0;
        this.lifecycle = Lifecycle.PLAYING;
    }

    // ---- dati statici di partita -------------------------------------------

    public Board getBoard() { return board; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<AlchemicFormula> getFormulas() { return formulas; }
    public AlchemicMapping getAlchemicMapping() { return alchemicMapping; }

    // ---- turno --------------------------------------------------------------

    public List<Player> getPlayers() { return List.copyOf(players); }

    public Player getCurrentPlayer() {
        if (lifecycle == Lifecycle.SETUP)
            throw new IllegalStateException("Partita non ancora avviata.");
        return players.get(currentPlayerIndex);
    }

    public void advanceTurn() {
        if (lifecycle != Lifecycle.PLAYING)
            throw new IllegalStateException("Turno avanzabile solo durante PLAYING.");
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
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
