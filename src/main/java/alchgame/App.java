package alchgame;

import alchgame.model.*;
import alchgame.service.*;

import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        DeductionGrid grid = new DeductionGrid("init");
        ResultsTriangle triangle = new ResultsTriangle();
        PrivateLaboratory lab = new PrivateLaboratory(grid, triangle);
        PublicPlayerBoard board = new PublicPlayerBoard();
        Student student = new Student(StudentStatus.UNHAPPY);
        Player player = new Player(1, 0, lab, board);
        AlchemicAlgorithm algorithm = new AlchemicAlgorithm();

        GameEngine engine = new GameEngine(student, lab, board, triangle, grid, algorithm, player);
        AlchGame alchGame = new AlchGame(engine);
        ExperimentHandler handler = new ExperimentHandler();

        Ingredient ing1 = new Ingredient("Toad", new AlchemicFormula(List.of()));
        Ingredient ing2 = new Ingredient("Mushroom", new AlchemicFormula(List.of()));
        lab.getIngredients().add(ing1);
        lab.getIngredients().add(ing2);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Target? (1=STUDENT, 2=PLAYER)");
            String targetInput = scanner.nextLine().trim();
            Target target = "1".equals(targetInput) ? Target.STUDENT : Target.PLAYER;

            StartExperimentResponse start = handler.startExperiment(target, alchGame);
            if (start.isPaymentRequired()) {
                System.out.println("Payment required. Pay 1 gold? (y/n)");
                String pay = scanner.nextLine().trim().toLowerCase();
                if (!pay.startsWith("y")) {
                    handler.renounceExperiment(alchGame);
                    System.out.println("Experiment renounced");
                    return;
                }
                StartExperimentResponse paid = handler.payGold(alchGame);
                if (paid.isPaymentRequired()) {
                    System.out.println("Not enough gold. Experiment renounced.");
                    handler.renounceExperiment(alchGame);
                    return;
                }
                start = paid;
            }

            System.out.println("Available ingredients:");
            List<Ingredient> available = start.getAvailableIngredients();
            for (int i = 0; i < available.size(); i++) {
                System.out.println((i + 1) + ". " + available.get(i).getName());
            }

            System.out.println("Choose ingredient 1 (index):");
            int idx1 = Integer.parseInt(scanner.nextLine().trim()) - 1;
            System.out.println("Choose ingredient 2 (index):");
            int idx2 = Integer.parseInt(scanner.nextLine().trim()) - 1;

            Ingredient chosen1 = available.get(idx1);
            Ingredient chosen2 = available.get(idx2);

            ConductExperimentResponse result = handler.conductExperiment(chosen1, chosen2, alchGame);
            Potion potion = result.getPotion();
            if (potion != null) {
                System.out.println("Potion result: " + potion.getColor() + " " + potion.getSign());
            } else {
                System.out.println("Potion result: null");
            }
        }
    }
}
