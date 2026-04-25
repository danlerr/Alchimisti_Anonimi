package alchgame.model;

import java.util.ArrayList;
import java.util.List;

public class Player implements Target {

    private final String name;
    private int gold;
    private int reputation;
    private int actionCubes;
    private final int maxActionCubes;
    private final PrivateLaboratory  privateLaboratory;
    private final PublicPlayerBoard  publicPlayerBoard;
    private final List<Experiment>   conductedExperiments = new ArrayList<>();
    private final List<Favor>        favorCards           = new ArrayList<>();

    public Player(String name, int gold, int reputation, int actionCubes,
                  PrivateLaboratory privateLaboratory,
                  PublicPlayerBoard publicPlayerBoard) {
        this.name              = name;
        this.gold              = gold;
        this.reputation        = reputation;
        this.actionCubes       = actionCubes;
        this.maxActionCubes    = actionCubes;
        this.privateLaboratory = privateLaboratory;
        this.publicPlayerBoard = publicPlayerBoard;
    }

    public String getName() { return name; }

    // ---- action cubes -------------------------------------------------------

    public int getActionCubes() { return actionCubes; }

    public void removeActionCube(int amount) {
        if (actionCubes < amount)
            throw new IllegalStateException("Cubi azione insufficienti.");
        actionCubes -= amount;
    }

    public void restoreActionCubes() {
        actionCubes = maxActionCubes;
    }

    // ---- gold ---------------------------------------------------------------

    public int getGold() { return gold; }

    public void removeGold(int amount) {
        if (gold < amount)
            throw new IllegalStateException("Oro insufficiente.");
        gold -= amount;
    }

    // ---- reputation ---------------------------------------------------------

    public int getReputation() { return reputation; }


    // ---- Target -------------------------------------------------------------

    @Override
    public boolean requiresPayment() { return false; }

    @Override
    public void applyEffect(Potion potion) {
        if (!potion.isNegative()) return;
        switch (potion.getColor()) {
            case BLUE  -> reputation = Math.max(0, reputation - 1); // Pazzia → perdi reputazione
            case RED   -> { /* TODO next iteration: Veleno → perdi cubi azione */ } //1 cubo giocatore va in ospedale
            case GREEN -> { /* TODO next iteration: Paralisi → perdi priorità turno */ } //segnalino giocatore nella zona paralisi
        }
    }

    // ---- relazioni ----------------------------------------------------------

    public PrivateLaboratory getPrivateLaboratory() { return privateLaboratory; }

    public PublicPlayerBoard getPublicPlayerBoard() { return publicPlayerBoard; }

    public void publishExperimentResult(Potion potion) {
        publicPlayerBoard.publishExperimentResult(potion);
    }

    public void updateLab(Ingredient i1, Ingredient i2, Potion potion) {
        privateLaboratory.updatePrivateLab(i1, i2, potion);
    }

    public Experiment recordExperiment(Target target, Ingredient i1, Ingredient i2, Potion potion) {
        Experiment experiment = Experiment.createExperiment(target, i1, i2, potion);
        publishExperimentResult(potion);
        updateLab(i1, i2, potion);
        conductedExperiments.add(experiment);
        return experiment;
    }

    public List<Ingredient> getIngredientsFromLab() {
        return privateLaboratory.getIngredients();
    }

    public void addIngredient(Ingredient ingredient) {
        privateLaboratory.addIngredient(ingredient);
    }

    public void addFavor(Favor favor) {
        favorCards.add(favor);
    }

    public List<Favor> getFavorCards() { return List.copyOf(favorCards); }

    public void addExperiment(Experiment e) { conductedExperiments.add(e); }

    public List<Experiment> getConductedExperiments() { return List.copyOf(conductedExperiments); }

    @Override
    public String toString() {
        return "Player{name='" + name + "', gold=" + gold + ", reputation=" + reputation + "}";
    }
}