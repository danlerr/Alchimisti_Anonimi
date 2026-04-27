package alchgame.model;

import java.util.ArrayList;
import java.util.List;

import alchgame.model.effect.PotionEffectStrategyFactory;

public class Player implements Target {

    private final String name;
    private int gold;
    private int reputation;
    private int actionCubes;
    private int nextRoundCubeModifier  = 0;
    private int nextRoundPendingFavors = 0;
    private boolean paralyzedNextRound = false;
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
        actionCubes = Math.max(0, maxActionCubes + nextRoundCubeModifier);
        nextRoundCubeModifier = 0;
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

    public void changeReputation(int delta) {
        reputation = Math.max(0, reputation + delta);
    }

    // ---- effetti pianificati per il prossimo round --------------------------

    public void scheduleCubeModifier(int delta) { this.nextRoundCubeModifier += delta; }

    public void scheduleFavor(int n) { this.nextRoundPendingFavors += n; }

    public void scheduleParalysis() { this.paralyzedNextRound = true; }

    public boolean isParalyzed() { return paralyzedNextRound; }

    public int consumePendingFavors() {
        int n = nextRoundPendingFavors;
        nextRoundPendingFavors = 0;
        return n;
    }

    public void clearParalysis() { this.paralyzedNextRound = false; }

    // ---- Target -------------------------------------------------------------

    @Override
    public boolean requiresPayment() { return false; }

    @Override
    public void applyEffect(Potion potion) {
        PotionEffectStrategyFactory.from(potion).apply(this);
    }

    // ---- relazioni ----------------------------------------------------------

    public PrivateLaboratory getPrivateLaboratory() { return privateLaboratory; }

    public PublicPlayerBoard getPublicPlayerBoard() { return publicPlayerBoard; }

    public void publishExperimentResult(Potion potion) {
        publicPlayerBoard.publishExperimentResult(potion);
    }

    public void updateLab(Ingredient i1, Ingredient i2, Potion potion) {
        privateLaboratory.applyExperimentResult(i1, i2, potion);
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