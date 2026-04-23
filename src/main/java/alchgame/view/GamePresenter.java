package alchgame.view;

import alchgame.GameConfig;
import alchgame.controller.ExperimentHandler;
import alchgame.model.*;
import alchgame.service.AlchGame;

import java.util.List;

public class GamePresenter {

    private final ExperimentHandler handler;
    private final GameView view;
    private final Player player;
    private final Student student;

    public GamePresenter(AlchGame alchGame, ExperimentHandler handler, GameView view) {
        this.handler = handler;
        this.view    = view;
        this.player  = alchGame.getCurrentPlayer();
        this.student = (Student) alchGame.getTarget(GameConfig.TARGET_STUDENT_ID);
    }

    public void start() {
        while (true) {
            view.clearScreen();
            view.printHeader();
            view.printStatus(
                    player.getGold(),
                    player.getReputation(),
                    player.getConductedExperiments().size(),
                    player.getPrivateLaboratory().getIngredients().size());

            switch (view.showMainMenu()) {
                case 1 -> runExperimentFlow();
                case 2 -> runLaboratorio();
                case 3 -> runTabellone();
                case 4 -> runTargetStatus();
                case 0 -> { view.showGoodbye(); return; }
                default -> view.showError("Scelta non valida.");
            }
        }
    }

    private void runExperimentFlow() {
        view.clearScreen();
        view.printSection("INIZIA ESPERIMENTO");

        Integer targetChoice = view.askTargetChoice();
        if (targetChoice == null) return;
        String targetId = targetChoice == 1 ? GameConfig.TARGET_STUDENT_ID : GameConfig.TARGET_SELF_ID;

        boolean needsPayment = handler.paymentCheck(targetId);
        List<Ingredient> available;

        if (needsPayment) {
            if (!view.askPaymentConfirm(player.getGold())) {
                view.pause("  Esperimento annullato. Premi INVIO..."); return;
            }
            try {
                handler.payGold();
                available = handler.getIngredients();
                view.showPaymentSuccess(player.getGold());
            } catch (IllegalStateException e) {
                view.showError(e.getMessage()); return;
            }
        } else {
            available = handler.getIngredients();
        }

        if (available.size() < 2) {
            view.showError("Non hai abbastanza ingredienti!"); return;
        }

        view.showIngredients(available);
        Ingredient i1 = view.pickIngredient(available, "  Scegli il 1° ingrediente > ", null);
        if (i1 == null) return;
        Ingredient i2 = view.pickIngredient(available, "  Scegli il 2° ingrediente > ", i1);
        if (i2 == null) return;

        Potion potion = handler.conductExperiment(i1, i2);

        view.clearScreen();
        view.printSection("RISULTATO ESPERIMENTO");
        view.showExperimentResult(i1, i2, potion);

        if (GameConfig.TARGET_STUDENT_ID.equals(targetId))
            view.showStudentEffect(student.getState());
        else
            view.showPlayerEffect(player.getReputation());

        view.pause("\n  " + "Premi INVIO per continuare...");

        if (view.askDeductionConfirm()) runDeductionFlow();
    }

    private void runDeductionFlow() {
        DeductionGrid grid = player.getPrivateLaboratory().getDeductionGrid();
        view.clearScreen();
        view.printSection("GRIGLIA DI DEDUZIONE");
        view.showDeductionGrid(grid);

        int ingChoice = view.askIngredientIndex(grid.getIngredients().size());
        if (ingChoice <= 0) return;
        Ingredient ingredient = grid.getIngredients().get(ingChoice - 1);

        int alcChoice = view.askAlchemicIndex(grid.getAlchemics().size());
        if (alcChoice <= 0) return;
        AlchemicFormula alchemic = grid.getAlchemics().get(alcChoice - 1);

        if (grid.isExcluded(ingredient, alchemic)) {
            view.showError("Questo alchemico è già escluso per " + ingredient.getName() + ".");
            return;
        }
        grid.exclude(ingredient, alchemic);
        view.showDeductionSuccess(ingredient.getName(), alcChoice);
        view.pause("");
    }

    private void runLaboratorio() {
        view.clearScreen();
        view.printSection("LABORATORIO PRIVATO");
        PrivateLaboratory lab = player.getPrivateLaboratory();
        view.showLaboratorio(
                lab.getIngredients(),
                lab.getResultsTriangle().getAllResults(),
                lab.getDeductionGrid());
        view.pause("\n  Premi INVIO per tornare...");
    }

    private void runTabellone() {
        view.clearScreen();
        view.printSection("TABELLONE PUBBLICO");
        view.showTabellone(player.getPublicPlayerBoard().getPublishedResults());
        view.pause("\n  Premi INVIO per tornare...");
    }

    private void runTargetStatus() {
        view.clearScreen();
        view.printSection("STATO DEI TARGET");
        view.showTargetStatus(student.getState(), player.getGold(), player.getReputation());
        view.pause("\n  Premi INVIO per tornare...");
    }
}
