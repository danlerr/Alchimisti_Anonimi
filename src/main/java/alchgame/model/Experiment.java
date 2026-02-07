package alchgame.model;

import java.util.List;

public class Experiment {
    private final Player player;
    private final Student student;
    private boolean requiredPayment;
    private boolean paymentSatisfied;
    private List<Ingredient> ingredients;
    private Potion result;
    private boolean cancelled;
    private ExperimentStatus status = ExperimentStatus.IN_ATTESA;

    public Experiment(Player player, Student student) {
        this.player = player;
        this.student = student;
    }

    public Player getPlayer() {
        return player;
    }

    public Student getStudent() {
        return student;
    }

    public boolean isRequiredPayment() {
        return requiredPayment;
    }

    public void setRequiredPayment(boolean requiredPayment) {
        this.requiredPayment = requiredPayment;
        this.paymentSatisfied = !requiredPayment;
    }

    public boolean isPaymentSatisfied() {
        return paymentSatisfied;
    }

    public void setPaymentSatisfied(boolean paymentSatisfied) {
        this.paymentSatisfied = paymentSatisfied;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Potion getResult() {
        return result;
    }

    public void setResult(Potion result) {
        this.result = result;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        if (cancelled) {
            this.status = ExperimentStatus.ANNULLATO;
        }
    }

    public ExperimentStatus getStatus() {
        return status;
    }

    public void setStatus(ExperimentStatus status) {
        this.status = status;
    }

    public void completeExperiment(Ingredient ingredient1, Ingredient ingredient2, Potion potion) {
        this.ingredients = List.of(ingredient1, ingredient2);
        this.result = potion;
        this.status = ExperimentStatus.COMPLETATO;
    }
}
