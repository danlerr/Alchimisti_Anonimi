package alchgame.presentation;

import alchgame.application.ExperimentController;
import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.alchemy.Atom;
import alchgame.model.alchemy.Color;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.Potion;
import alchgame.model.player.DeductionGrid;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ExperimentActionPresenter {

    private final GameView view;
    private final ExperimentController experimentController;

    public ExperimentActionPresenter(GameView view, ExperimentController experimentController) {
        this.view = view;
        this.experimentController = experimentController;
    }

    public void run(Player player) {
        // 1. Scelta bersaglio
        List<String> targetIds = new ArrayList<>(experimentController.getTargets().keySet());
        view.showTargetOptions(targetIds);
        String targetId = view.promptTargetChoice(targetIds);

        // 2. Pagamento se richiesto
        if (experimentController.paymentCheck(targetId)) {
            view.showPaymentRequired();
            try {
                int remaining = experimentController.payGold(targetId);
                view.showPaymentResult(remaining);
            } catch (IllegalStateException e) {
                view.showInsufficientGold();
                return;
            }
        }

        // 3. Lista ingredienti dal player (query diretta al modello)
        List<Ingredient> ingredients = player.getIngredientsFromLab();
        if (ingredients.size() < 2) {
            view.showInvalidInput("Non hai abbastanza ingredienti per condurre un esperimento.");
            return;
        }
        view.showIngredients(ingredients.stream().map(Ingredient::getName).toList());

        // 4. Scelta dei due ingredienti
        Potion potion;
        while (true) {
            int firstChoice = view.promptIngredientChoice("Primo ingrediente", ingredients.size());
            int secondChoice;
            do {
                secondChoice = view.promptIngredientChoice("Secondo ingrediente", ingredients.size());
                if (secondChoice == firstChoice) view.showInvalidInput("Devi scegliere due ingredienti diversi.");
            } while (secondChoice == firstChoice);

            try {
                String firstId  = ingredients.get(firstChoice - 1).getId();
                String secondId = ingredients.get(secondChoice - 1).getId();
                potion = experimentController.conductExperiment(targetId, firstId, secondId);
                break;
            } catch (Exception e) {
                view.showInvalidInput(e.getMessage() + " Riscegli gli ingredienti.");
            }
        }

        // 5. Risultato
        String label     = potion.isNeutral() ? "NEUTRA" : potion.getColor().name() + " " + potion.getSign().name();
        String colorName = potion.isNeutral() ? "NEUTRAL" : potion.getColor().name();
        view.showPotionResult(label, colorName);

        // 6. Aggiornamento facoltativo della griglia di deduzione
        if (view.promptUpdateDeductionGrid()) {
            DeductionGrid grid = player.getDeductionGrid();
            List<Ingredient>      ings  = grid.getIngredients();
            List<AlchemicFormula> alcs  = grid.getAlchemics();

            List<String> ingNames = ings.stream().map(Ingredient::getName).toList();
            List<String> alcLabels = new ArrayList<>();
            for (int a = 0; a < alcs.size(); a++)
                alcLabels.add("  [" + (a + 1) + "]  " + formatFormula(alcs.get(a)));

            boolean[][] excluded = new boolean[alcs.size()][ings.size()];
            for (int a = 0; a < alcs.size(); a++)
                for (int i = 0; i < ings.size(); i++)
                    excluded[a][i] = grid.isExcluded(ings.get(i), alcs.get(a));

            view.showDeductionGrid(ingNames, alcLabels, excluded);

            int ingChoice = view.promptDeductionIngredientChoice(ings.size());
            int alcChoice = view.promptDeductionAlchemicChoice(alcs.size());
            try {
                Ingredient      ingredient = ings.get(ingChoice - 1);
                AlchemicFormula formula    = alcs.get(alcChoice - 1);
                experimentController.updateDeductionGrid(ingredient, formula);
                view.showExclusionResult(ingredient.getName(), "[" + alcChoice + "]");
            } catch (IllegalArgumentException e) {
                view.showInvalidInput("Esclusione non valida: " + e.getMessage());
            }
        }
    }

    private static String formatFormula(AlchemicFormula formula) {
        StringBuilder sb = new StringBuilder();
        for (Color color : Color.real()) {
            Atom atom = formula.getAtomByColor(color);
            if (atom == null) continue;
            if (!sb.isEmpty()) sb.append(' ');
            sb.append(colorChar(color))
              .append(':')
              .append(sizeChar(atom.getSize()))
              .append(signChar(atom.getSign()));
        }
        return sb.toString();
    }

    private static char colorChar(Color color) {
        return switch (color) {
            case RED   -> 'R';
            case GREEN -> 'G';
            case BLUE  -> 'B';
            default    -> '?';
        };
    }

    private static char sizeChar(alchgame.model.alchemy.Size size) {
        return switch (size) {
            case BIG   -> 'G';
            case SMALL -> 'P';
        };
    }

    private static char signChar(alchgame.model.alchemy.Sign sign) {
        return switch (sign) {
            case POSITIVE -> '+';
            case NEGATIVE -> '-';
            case NEUTRAL  -> '~';
        };
    }
}
