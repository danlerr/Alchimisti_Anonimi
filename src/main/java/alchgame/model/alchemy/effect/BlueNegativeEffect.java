package alchgame.model.alchemy.effect;

import alchgame.model.player.Player;

public class BlueNegativeEffect implements PotionEffectStrategy {

    private final int reputationModifier;

    public BlueNegativeEffect(int reputationModifier) {
        this.reputationModifier = reputationModifier;
    }

    @Override
    public void apply(Player player) {
        player.changeReputation(reputationModifier);
    }
}
