package alchgame.model;

public class Player {
    private int gold;
    private int reputation;
    private final PrivateLaboratory privateLaboratory;
    private final PublicPlayerBoard publicPlayerBoard;

    public Player(int gold, int reputation, PrivateLaboratory privateLaboratory, PublicPlayerBoard publicPlayerBoard) {
        this.gold = gold;
        this.reputation = reputation;
        this.privateLaboratory = privateLaboratory;
        this.publicPlayerBoard = publicPlayerBoard;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public PrivateLaboratory getPrivateLaboratory() {
        return privateLaboratory;
    }

    public PublicPlayerBoard getPublicPlayerBoard() {
        return publicPlayerBoard;
    }

    public boolean canPayGold(int amount) {
        return gold >= amount;
    }

    public void payGold(int amount) {
        if (!canPayGold(amount)) {
            throw new IllegalStateException("Not enough gold to pay " + amount);
        }
        this.gold -= amount;
    }
}
