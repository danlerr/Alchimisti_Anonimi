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

    // public void applyMalus(int malus) {
    //     this.reputation = Math.max(0, reputation - malus);
    // }

    // ---- Target -------------------------------------------------------------

    @Override
    public boolean requiresPayment() { return false; }

    @Override
    public void applyEffect(Potion potion) {
        if (potion.isNegative())
            if(potion.getColor() == Color.BLUE)
                //applica effetto pozione blue negativa;
            if(potion.getColor() == Color.GREEN)
                //applica effetto pozione green negativa;
            if(potion.getColor() == Color.RED)
                //applica effetto pozione red negativa;
    }

    // ---- relazioni ----------------------------------------------------------

    public PrivateLaboratory getPrivateLaboratory() { return privateLaboratory; }

    public PublicPlayerBoard getPublicPlayerBoard() { return publicPlayerBoard; }

    public void addExperiment(Experiment e) { conductedExperiments.add(e); }
    public List<Experiment> getConductedExperiments() { return List.copyOf(conductedExperiments); }

    @Override
    public String toString() {
        return "Player{gold=" + gold + ", reputation=" + reputation + "}";
    }
}
