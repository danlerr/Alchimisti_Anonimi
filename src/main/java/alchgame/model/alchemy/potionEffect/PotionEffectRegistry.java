package alchgame.model.alchemy.potionEffect;

import java.util.HashMap;
import java.util.Map;

import alchgame.model.alchemy.Color;
import alchgame.model.alchemy.Potion;
import alchgame.model.alchemy.Sign;

/** Registry che, data una Potion, ritorna la PotionEffectStrategy corrispondente. */
public final class PotionEffectRegistry {

    private record Key(Color color, Sign sign) {}
    private final Map<Key, PotionEffectStrategy> registry = new HashMap<>();
    private final PotionEffectStrategy NO_OP = player -> {};


    public void register (Color color, Sign sign, PotionEffectStrategy strategy){
        registry.put(new Key(color, sign), strategy);
    }

    public PotionEffectStrategy from(Potion potion) {
        if (potion.isNeutral()) return NO_OP;
        return registry.getOrDefault(new Key(potion.getColor(), potion.getSign()), NO_OP);
    }
}
