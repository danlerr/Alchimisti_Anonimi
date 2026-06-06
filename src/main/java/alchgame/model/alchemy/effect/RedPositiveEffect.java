package alchgame.model.alchemy.effect;
import alchgame.model.player.Player;

public class RedPositiveEffect implements PotionEffectStrategy {
    private final int cubeModifier;

    public RedPositiveEffect(int cubeModifier) {
        this.cubeModifier = cubeModifier;
    }

    @Override
    public void apply(Player player) {
        player.schedulePendingEffect((p, b) -> p.addActionCubes(this.cubeModifier));
    }
}