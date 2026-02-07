package it.univaq.alchimisti_anonimi.domain;

public class Player {
    private int gold;
    private int reputation;

    public Player(int gold, int reputation) {
        this.gold = gold;
        this.reputation = reputation;
    }

    public int getGold() {
        return gold;
    }

    public int getReputation() {
        return reputation;
    }

    public void addGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        this.gold += amount;
    }

    public void removeGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (gold < amount) {
            throw new IllegalStateException("not enough gold");
        }
        this.gold -= amount;
    }

    public void addReputation(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        this.reputation += amount;
    }
}
