package alchgame.model.effect;

import alchgame.model.Player;

public class RedPositiveEffect implements PotionEffectStrategy {
    @Override
    public void apply(Player player) {
        player.scheduleCubeModifier(+1);
    }
}
