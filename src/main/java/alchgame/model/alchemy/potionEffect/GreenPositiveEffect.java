package alchgame.model.alchemy.potionEffect;

import alchgame.model.player.Player;

public class GreenPositiveEffect implements PotionEffect {

    private final int favorModifier;

    public GreenPositiveEffect(int favorModifier) {
        this.favorModifier = favorModifier;
    }

    @Override
    public void apply(Player player) {
        player.schedulePendingEffect((p, b) -> b.dealFavors(p, this.favorModifier));
    }
}
