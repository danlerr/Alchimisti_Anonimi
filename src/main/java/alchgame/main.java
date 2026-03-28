package alchgame;

import alchgame.controller.ExperimentHandler;
import alchgame.service.AlchemicAlgorithm;
import alchgame.service.GameContext;
import alchgame.model.*;

import java.util.List;
import java.util.Map;

public class main {
    public static void main(String[] args) {

        // Setup ingredienti
        AlchemicFormula f1 = new AlchemicFormula(List.of(
            new Atom("red", "large", "+"),
            new Atom("blue", "small", "-"),
            new Atom("green", "medium", "+")
        ));
        AlchemicFormula f2 = new AlchemicFormula(List.of(
            new Atom("blue", "large", "+"),
            new Atom("red", "small", "+"),
            new Atom("green", "large", "+")
        ));

        Ingredient ing1 = new Ingredient("Erba Lunare", f1);
        Ingredient ing2 = new Ingredient("Radice Rossa", f2);

        // Setup laboratorio
        DeductionGrid grid = new DeductionGrid();
        ResultsTriangle triangle = new ResultsTriangle();
        PrivateLaboratory lab = new PrivateLaboratory(List.of(ing1, ing2), grid, triangle);
        PublicPlayerBoard board = new PublicPlayerBoard();

        // Setup giocatore e studente
        Player player = new Player(5, 10, lab, board);
        Student student = new Student();

        // Setup contesto
        GameContext ctx = new GameContext(player, Map.of("studente1", student));
        AlchemicAlgorithm algo = new AlchemicAlgorithm();
        ExperimentHandler handler = new ExperimentHandler(ctx, algo);

        // Esegui esperimento
        Object result = handler.startExperiment("studente1");
        System.out.println("startExperiment → " + result.getClass().getSimpleName());

        handler.conductExperiment(ing1, ing2);
    }
}