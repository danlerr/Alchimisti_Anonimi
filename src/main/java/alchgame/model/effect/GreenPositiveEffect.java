package alchgame.model.effect;

import alchgame.model.Player;

public class GreenPositiveEffect implements PotionEffectStrategy {
    @Override
    public void apply(Player player) {
        player.scheduleFavor(1);
    }
}
