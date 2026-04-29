package alchgame.model.player;

import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.Potion;
import alchgame.model.board.Favor;
import java.util.ArrayList;
import java.util.List;

import alchgame.model.game.Target;
import alchgame.model.effect.PotionEffectRegistry;

public class Player implements Target {

    private final String name;
    private int gold;
    private int reputation;
    private int actionCubes;
    private final PendingEffects pendingEffects = new PendingEffects();
    private final PrivateLaboratory  privateLaboratory;
    private final PublicPlayerBoard  publicPlayerBoard;
    private final List<Favor>        favorCards           = new ArrayList<>();

    public Player(String name, int gold, int reputation, int actionCubes,
                  PrivateLaboratory privateLaboratory,
                  PublicPlayerBoard publicPlayerBoard) {
        this.name              = name;
        this.gold              = gold;
        this.reputation        = reputation;
        this.actionCubes       = actionCubes;
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

    public void restoreActionCubes(int base) {
        actionCubes = Math.max(0, base + pendingEffects.consumeCubeModifier());
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

    public void scheduleCubeModifier(int delta) { pendingEffects.scheduleCubeModifier(delta); }

    public void scheduleFavor(int n) { pendingEffects.scheduleFavor(n); }

    public void scheduleParalysis() { pendingEffects.scheduleParalysis(); }

    public boolean isParalyzed() { return pendingEffects.isParalyzed(); }

    public int consumePendingFavors() { return pendingEffects.consumePendingFavors(); }

    public void clearParalysis() { pendingEffects.clearParalysis(); }

    // ---- Target -------------------------------------------------------------

    @Override
    public boolean requiresPayment() { return false; }

    @Override
    public void applyEffect(Potion potion) {
        PotionEffectRegistry.from(potion).apply(this);
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


    @Override
    public String toString() {
        return "Player{name='" + name + "', gold=" + gold + ", reputation=" + reputation + "}";
    }
}
