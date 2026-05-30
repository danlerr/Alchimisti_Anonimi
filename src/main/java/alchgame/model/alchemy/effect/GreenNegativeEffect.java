package alchgame.model.alchemy.effect;
import alchgame.model.player.Player;

public class GreenNegativeEffect implements PotionEffectStrategy {
    private final int favorModifier;

    public GreenNegativeEffect(int favorModifier) {
        this.favorModifier = favorModifier;
    }

    @Override
    public void apply(Player player) {
        player.scheduleFavor(this.favorModifier);
    }
}