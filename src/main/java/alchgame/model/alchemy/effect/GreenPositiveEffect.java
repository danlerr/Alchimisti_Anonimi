package alchgame.model.alchemy.effect;
import alchgame.model.player.Player;

public class GreenPositiveEffect implements PotionEffectStrategy {
    private final int favorModifier;

    public GreenPositiveEffect(int favorModifier) {
        this.favorModifier = favorModifier;
    }

    @Override
    public void apply(Player player) {
        player.scheduleFavor(this.favorModifier);
    }
}