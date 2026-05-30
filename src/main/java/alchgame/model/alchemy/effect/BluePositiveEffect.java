package alchgame.model.alchemy.effect;
import alchgame.model.player.Player;

public class BluePositiveEffect implements PotionEffectStrategy {
    private final int reputationModifier;

    public BluePositiveEffect(int reputationModifier) {
        this.reputationModifier = reputationModifier;
    }

    @Override
    public void apply(Player player) {
        player.changeReputation(this.reputationModifier);
    }
}