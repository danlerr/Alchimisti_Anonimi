package alchgame.model.effect;

import java.util.Map;

import alchgame.model.Color;
import alchgame.model.Potion;
import alchgame.model.Sign;

/** Factory che, data una Potion, ritorna la PotionEffectStrategy corrispondente. */
public final class PotionEffectFactory {

    private record Key(Color color, Sign sign) {}

    private static final PotionEffectStrategy NO_OP = player -> {};

    private static final Map<Key, PotionEffectStrategy> REGISTRY = Map.of(
        new Key(Color.BLUE,  Sign.NEGATIVE), new BlueNegativeEffect(),
        new Key(Color.RED,   Sign.NEGATIVE), new RedNegativeEffect(),
        new Key(Color.GREEN, Sign.NEGATIVE), new GreenNegativeEffect(),
        new Key(Color.BLUE,  Sign.POSITIVE), new BluePositiveEffect(),
        new Key(Color.RED,   Sign.POSITIVE), new RedPositiveEffect(),
        new Key(Color.GREEN, Sign.POSITIVE), new GreenPositiveEffect()
    );

    private PotionEffectFactory() {}

    public static PotionEffectStrategy from(Potion potion) {
        if (potion.isNeutral()) return NO_OP;
        return REGISTRY.getOrDefault(new Key(potion.getColor(), potion.getSign()), NO_OP);
    }
}
