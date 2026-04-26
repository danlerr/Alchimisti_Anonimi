package alchgame.model.effect;

import alchgame.model.Player;

public class BlueNegativeEffect implements PotionEffectStrategy {
    @Override
    public void apply(Player player) {
        player.changeReputation(-1);
    }
}
