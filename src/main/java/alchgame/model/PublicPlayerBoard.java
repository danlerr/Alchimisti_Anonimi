package alchgame.model;

public class PublicPlayerBoard {
    private Potion lastPublishedPotion;

    public void publishResult(Potion potion) {
        this.lastPublishedPotion = potion;
    }

    public Potion getLastPublishedPotion() {
        return lastPublishedPotion;
    }
}
