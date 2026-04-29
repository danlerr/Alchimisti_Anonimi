package alchgame.model.effect;

import alchgame.model.player.Player;

public class RedNegativeEffect implements PotionEffectStrategy {
    @Override
    public void apply(Player player) {
        player.scheduleCubeModifier(-1);
    }
}
