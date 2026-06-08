package alchgame.model.alchemy.potionEffect;

import java.util.HashMap;
import java.util.Map;

import alchgame.model.alchemy.Color;
import alchgame.model.alchemy.Potion;
import alchgame.model.alchemy.Sign;

/** Registry che, data una Potion, ritorna la PotionEffect corrispondente. */
public final class PotionEffectRegistry {

    private record Key(Color color, Sign sign) {}
    private final Map<Key, PotionEffect> registry = new HashMap<>();
    private final PotionEffect NO_OP = player -> {};


    public void register (Color color, Sign sign, PotionEffect strategy){
        registry.put(new Key(color, sign), strategy);
    }

    public PotionEffect from(Potion potion) {
        if (potion.isNeutral()) return NO_OP;
        return registry.getOrDefault(new Key(potion.getColor(), potion.getSign()), NO_OP);
    }
}
