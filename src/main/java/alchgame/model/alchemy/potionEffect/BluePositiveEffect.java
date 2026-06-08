package alchgame.model.alchemy.potionEffect;

import alchgame.model.player.Player;

public class BluePositiveEffect implements PotionEffect {

    private final int reputationModifier;

    public BluePositiveEffect(int reputationModifier) {
        this.reputationModifier = reputationModifier;
    }

    @Override
    public void apply(Player player) {
        player.changeReputation(this.reputationModifier);
    }
}
