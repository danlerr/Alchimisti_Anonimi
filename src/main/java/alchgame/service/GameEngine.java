package alchgame.service;

import alchgame.model.*;

import java.util.List;

public class GameEngine {
    private final Student student;
    private final PrivateLaboratory privateLaboratory;
    private final PublicPlayerBoard publicPlayerBoard;
    private final ResultsTriangle resultsTriangle;
    private final DeductionGrid deductionGrid;
    private final AlchemicAlgorithm alchemicAlgorithm;
    private final Player currentPlayer;

    private Experiment currentExperiment;

    public GameEngine(Student student,
                      PrivateLaboratory privateLaboratory,
                      PublicPlayerBoard publicPlayerBoard,
                      ResultsTriangle resultsTriangle,
                      DeductionGrid deductionGrid,
                      AlchemicAlgorithm alchemicAlgorithm,
                      Player currentPlayer) {
        this.student = student;
        this.privateLaboratory = privateLaboratory;
        this.publicPlayerBoard = publicPlayerBoard;
        this.resultsTriangle = resultsTriangle;
        this.deductionGrid = deductionGrid;
        this.alchemicAlgorithm = alchemicAlgorithm;
        this.currentPlayer = currentPlayer;
    }

    public StartExperimentResponse startExperiment(Target target) {
        currentExperiment = createExperiment(target);
        if (target == Target.STUDENT) {
            StudentStatus status = student.getStatus();
            if (status == StudentStatus.UNHAPPY) {
                currentExperiment.setRequiredPayment(true);
                currentExperiment.setPaymentSatisfied(false);
                return StartExperimentResponse.paymentRequired(currentExperiment);
            }
        }

        currentExperiment.setRequiredPayment(false);
        currentExperiment.setPaymentSatisfied(true);
        List<Ingredient> ingredients = privateLaboratory.getIngredients();
        return StartExperimentResponse.ready(currentExperiment, ingredients);
    }

    public StartExperimentResponse payGold() {
        if (currentExperiment == null) {
            return StartExperimentResponse.paymentNotRequired();
        }

        if (!currentExperiment.isRequiredPayment()) {
            return StartExperimentResponse.paymentNotRequired();
        }

        if (!currentPlayer.canPayGold(1)) {
            return StartExperimentResponse.paymentRequired(currentExperiment);
        }

        currentPlayer.payGold(1);
        currentExperiment.setPaymentSatisfied(true);

        List<Ingredient> ingredients = privateLaboratory.getIngredients();
        return StartExperimentResponse.ready(currentExperiment, ingredients);
    }

    public void renounceExperiment() {
        if (currentExperiment != null) {
            currentExperiment.setCancelled(true);
        }
        currentExperiment = null;
    }

    public ConductExperimentResponse conductExperiment(Ingredient ingredient1, Ingredient ingredient2) {
        if (currentExperiment == null) {
            return new ConductExperimentResponse(null);
        }
        if (currentExperiment.isRequiredPayment() && !currentExperiment.isPaymentSatisfied()) {
            return new ConductExperimentResponse(null);
        }

        Potion potion = alchemicAlgorithm.computePotion(ingredient1, ingredient2);

        currentExperiment.completeExperiment(ingredient1, ingredient2, potion);

        publicPlayerBoard.publishResult(potion);
        privateLaboratory.updateLab(ingredient1, ingredient2, potion);

        applyImmediateConsequences(potion);

        return new ConductExperimentResponse(potion);
    }

    private Experiment createExperiment(Target target) {
        Student experimentStudent = target == Target.STUDENT ? student : null;
        return new Experiment(currentPlayer, experimentStudent);
    }

    private void applyImmediateConsequences(Potion potion) {
        // TODO: apply effects based on experiment type and potion sign
    }

    public Experiment getCurrentExperiment() {
        return currentExperiment;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
