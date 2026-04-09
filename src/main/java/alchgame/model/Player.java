package alchgame.model;

import java.util.ArrayList;
import java.util.List;

public class Player implements Target {

    private int gold;
    private int reputation;
    private final PrivateLaboratory  privateLaboratory;
    private final PublicPlayerBoard  publicPlayerBoard;
    private final List<Experiment>   conductedExperiments = new ArrayList<>();

    public Player(int gold, int reputation,
                  PrivateLaboratory privateLaboratory,
                  PublicPlayerBoard publicPlayerBoard) {
        this.gold              = gold;
        this.reputation        = reputation;
        this.privateLaboratory = privateLaboratory;
        this.publicPlayerBoard = publicPlayerBoard;
    }

    // ---- gold ---------------------------------------------------------------

    public int getGold() { return gold; }

    public boolean removeGold(int amount) {
        if (gold < amount) return false;
        gold -= amount;
        return true;
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

    public List<Ingredient> getIngredientsFromLab() {
        return privateLaboratory.getIngredients();
    }

    public void addExperiment(Experiment e) { conductedExperiments.add(e); }

    public List<Experiment> getConductedExperiments() { return List.copyOf(conductedExperiments); }

    @Override
    public String toString() {
        return "Player{gold=" + gold + ", reputation=" + reputation + "}";
    }
}
