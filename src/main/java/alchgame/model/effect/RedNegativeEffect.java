package alchgame.model.effect;

import alchgame.model.Player;

public class RedNegativeEffect implements PotionEffectStrategy {
    @Override
    public void apply(Player player) {
        player.scheduleCubeModifier(-1);
    }
}
