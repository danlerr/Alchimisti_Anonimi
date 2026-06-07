package alchgame.model.alchemy.potionEffect;

import alchgame.model.player.Player;

public class RedNegativeEffect implements PotionEffectStrategy {

    private final int cubeModifier;

    public RedNegativeEffect(int cubeModifier) {
        this.cubeModifier = cubeModifier;
    }

    @Override
    public void apply(Player player) {
        player.schedulePendingEffect((p, b) -> p.addActionCubes(this.cubeModifier));
    }
}
