package alchgame.model.alchemy.potionEffect;

import alchgame.model.player.Player;

public class GreenNegativeEffect implements PotionEffect {

    @Override
    public void apply(Player player) {
        player.scheduleParalysis();
    }
}
