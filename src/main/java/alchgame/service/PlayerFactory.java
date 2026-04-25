package alchgame.service;

import alchgame.model.AlchemicFormula;
import alchgame.model.DeductionGrid;
import alchgame.model.Ingredient;
import alchgame.model.Player;
import alchgame.model.PrivateLaboratory;
import alchgame.model.PublicPlayerBoard;
import alchgame.model.ResultsTriangle;

import java.util.ArrayList;
import java.util.List;

/**
 * PlayerFactory — Pure Fabrication (GRASP) responsabile della creazione dei Player.
 *
 * Conserva i dati condivisi tra tutti i giocatori della partita (ingredienti e
 * alchemici), che servono a costruire la DeductionGrid di ciascun Player.
 * I valori "economici" (oro, reputazione, cubi azione) sono passati dall'esterno
 * (tipicamente da AlchGame, che li legge dalla configurazione).
 *
 * Disaccoppia Player da PrivateLaboratory, DeductionGrid, ResultsTriangle e
 * PublicPlayerBoard: il modello non sa più come si assembla un Player, lo sa
 * solo questa factory.
 */
public class PlayerFactory {

    private final List<Ingredient> ingredients;
    private final List<AlchemicFormula> formulas;

    public PlayerFactory(List<Ingredient> ingredients, List<AlchemicFormula> formulas) {
        this.ingredients = List.copyOf(ingredients);
        this.formulas    = List.copyOf(formulas);
    }

    /**
     * Crea un nuovo Player con laboratorio privato, griglia di deduzione,
     * triangolo dei risultati e tabellone pubblico inizializzati a vuoto.
     */
    public Player createPlayer(String name, int gold, int reputation, int actionCubes) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome giocatore vuoto.");
        DeductionGrid     grid = new DeductionGrid(ingredients, formulas);
        PrivateLaboratory lab  = new PrivateLaboratory(new ArrayList<>(), grid, new ResultsTriangle());
        return new Player(name, gold, reputation, actionCubes, lab, new PublicPlayerBoard());
    }
}