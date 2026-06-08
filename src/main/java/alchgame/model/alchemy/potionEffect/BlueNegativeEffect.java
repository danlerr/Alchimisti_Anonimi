package alchgame.model.alchemy.potionEffect;

import alchgame.model.player.Player;

public class BlueNegativeEffect implements PotionEffect {

    private final int reputationModifier;

    public BlueNegativeEffect(int reputationModifier) {
        this.reputationModifier = reputationModifier;
    }

    @Override
    public void apply(Player player) {
        player.changeReputation(reputationModifier);
    }
}
