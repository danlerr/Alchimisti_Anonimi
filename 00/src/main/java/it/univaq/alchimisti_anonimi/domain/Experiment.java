package it.univaq.alchimisti_anonimi.domain;

import java.util.Objects;

public class Experiment {
    private final ExperimentTarget target;
    private final Student student;
    private final boolean paymentRequired;
    private boolean paymentSatisfied;
    private ExperimentStatus status;
    private IngredientCard ingredient1;
    private IngredientCard ingredient2;
    private Potion result;

    public Experiment(ExperimentTarget target, Student student) {
        this.target = Objects.requireNonNull(target);
        this.student = student;
        this.paymentRequired = target == ExperimentTarget.STUDENT
                && student != null
                && student.getState() == StudentState.UNHAPPY;
        this.paymentSatisfied = !paymentRequired;
        this.status = ExperimentStatus.PENDING;
    }

    public ExperimentTarget getTarget() {
        return target;
    }

    public boolean isPaymentRequired() {
        return paymentRequired;
    }

    public boolean isPaymentSatisfied() {
        return paymentSatisfied;
    }

    public void setPaymentSatisfied(boolean paymentSatisfied) {
        if (!paymentRequired) {
            return;
        }
        this.paymentSatisfied = paymentSatisfied;
    }

    public ExperimentStatus getStatus() {
        return status;
    }

    public void setCancelled(boolean cancelled) {
        if (cancelled) {
            this.status = ExperimentStatus.CANCELLED;
        }
    }

    public void completeExperiment(IngredientCard ingredient1, IngredientCard ingredient2, Potion result) {
        if (status != ExperimentStatus.PENDING) {
            throw new IllegalStateException("Experiment not pending");
        }
        this.ingredient1 = Objects.requireNonNull(ingredient1);
        this.ingredient2 = Objects.requireNonNull(ingredient2);
        this.result = Objects.requireNonNull(result);
        this.status = ExperimentStatus.COMPLETED;
    }

    public IngredientCard getIngredient1() {
        return ingredient1;
    }

    public IngredientCard getIngredient2() {
        return ingredient2;
    }

    public Potion getResult() {
        return result;
    }
}
