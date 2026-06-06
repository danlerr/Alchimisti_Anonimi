package alchgame.model.alchemy.effect;
import alchgame.model.player.Player;

public class GreenNegativeEffect implements PotionEffectStrategy {

    @Override
    public void apply(Player player) {
        player.scheduleParalysis();
    }
}
