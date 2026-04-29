package alchgame.model.effect;

import alchgame.model.player.Player;

public class RedPositiveEffect implements PotionEffectStrategy {
    @Override
    public void apply(Player player) {
        player.scheduleCubeModifier(+1);
    }
}
