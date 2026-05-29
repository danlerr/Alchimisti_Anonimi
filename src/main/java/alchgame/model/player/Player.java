package alchgame.model.player;

import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.Potion;
import alchgame.model.board.Favor;
import alchgame.model.effect.PotionEffectRegistry;
import alchgame.model.game.Target;

import java.util.ArrayList;
import java.util.List;

public class Player implements Target {

    private final String name;
    private int gold;
    private int reputation;
    private int actionCubes;
    private final PendingEffects pendingEffects = new PendingEffects();
    private final PrivateLaboratory privateLaboratory;
    private final PublicPlayerBoard publicPlayerBoard;
    private final List<Favor> favorCards = new ArrayList<>();

    public Player(String name, int gold, int reputation, int actionCubes,
                  PrivateLaboratory privateLaboratory,
                  PublicPlayerBoard publicPlayerBoard) {
        this.name = name;
        this.gold = gold;
        this.reputation = reputation;
        this.actionCubes = actionCubes;
        this.privateLaboratory = privateLaboratory;
        this.publicPlayerBoard = publicPlayerBoard;
    }

    // --- Getters & Setters ---

    public String getName() { 
        return name; 
    }

    public int getGold() { 
        return gold; 
    }

    public int getReputation() { 
        return reputation; 
    }

    public int getActionCubes() { 
        return actionCubes; 
    }

    // --- Gestione Risorse (Oro, Reputazione, Cubi Azione) ---

    public void addGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Quantità d'oro non valida.");
        }
        gold += amount;
    }

    public void removeGold(int amount) {
        if (gold < amount) {
            throw new IllegalStateException("Oro insufficiente.");
        }
        gold -= amount;
    }

    public void changeReputation(int delta) {
        reputation = Math.max(0, reputation + delta);
    }

    public void removeActionCube(int amount) {
        if (actionCubes < amount) {
            throw new IllegalStateException("Cubi azione insufficienti.");
        }
        actionCubes -= amount;
    }

    public void restoreActionCubes(int base) {
        actionCubes = Math.max(0, base + pendingEffects.consumeCubeModifier());
    }

    // --- Effetti ---

    public void scheduleCubeModifier(int delta) { 
        pendingEffects.scheduleCubeModifier(delta); 
    }

    public void scheduleFavor(int n) {
        pendingEffects.scheduleFavor(n);
    }

    public void scheduleParalysis() { 
        pendingEffects.scheduleParalysis(); 
    }

    public boolean isParalyzed() { 
        return pendingEffects.isParalyzed(); 
    }

    public int consumePendingFavors() { 
        return pendingEffects.consumePendingFavors(); 
    }

    public void clearParalysis() { 
        pendingEffects.clearParalysis(); 
    }

    // --- Interfaccia Target & Pozioni ---

    @Override
    public boolean requiresPayment() { 
        return false; 
    }

    @Override
    public void applyEffect(Potion potion) {
        PotionEffectRegistry.from(potion).apply(this);
    }

    @Override
    public int getPaymentAmount() {
        return 0;
    }

    // --- Interazione con i Tabelloni ---

    public PublicPlayerBoard getPublicPlayerBoard() { 
        return publicPlayerBoard; 
    }

    public void publishExperimentResult(Potion potion) {
        publicPlayerBoard.publishExperimentResult(potion);
    }

    // --- Gestione Laboratorio Privato (Ingredienti) ---

    public boolean canExperiment() {
        return privateLaboratory.getIngredients().size() >= 2;
    }

    public List<Ingredient> getIngredientsFromLab() {
        return privateLaboratory.getIngredients();
    }

    public Ingredient findIngredientById(String id) {
        return privateLaboratory.findById(id);
    }

    public void addIngredient(Ingredient ingredient) {
        privateLaboratory.addIngredient(ingredient);
    }

    public void removeIngredient(Ingredient ingredient) {
        privateLaboratory.removeIngredient(ingredient);
    }

    public void updateLab(Ingredient i1, Ingredient i2, Potion potion) {
        privateLaboratory.applyExperimentResult(i1, i2, potion);
    }

    // --- Griglia di Deduzione ---

    public DeductionGrid getDeductionGrid() {
        return privateLaboratory.getDeductionGrid();
    }

    public void excludeFromDeductionGrid(Ingredient ingredient, AlchemicFormula formula) {
        privateLaboratory.getDeductionGrid().exclude(ingredient, formula);
    }

    // --- Carte Favore ---

    public void addFavor(Favor favor) {
        favorCards.add(favor);
    }

    public List<Favor> getFavorCards() { 
        return List.copyOf(favorCards); 
    }

    // --- Metodi Object Override ---

    @Override
    public String toString() {
        return "Player{name='" + name + "', gold=" + gold + ", reputation=" + reputation + "}";
    }
}