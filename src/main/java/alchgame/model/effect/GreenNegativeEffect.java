package alchgame.model.effect;

import alchgame.model.Player;

public class GreenNegativeEffect implements PotionEffectStrategy {
    @Override
    public void apply(Player player) {
        player.scheduleParalysis();
    }
}
