package alchgame.model.alchemy;

public class Potion {

    private final Color color;
    private final Sign sign; // "+" positiva | "-" negativa

    private Potion(Color color, Sign sign) {
        this.color = color;
        this.sign  = sign;
    }

    public static Potion createPotion(Color color, Sign sign) {
        return new Potion(color, sign);
    }

    public static Potion createNeutralPotion() {
        return new Potion(Color.NONE, Sign.NEUTRAL);
    }

    public Color getColor() {
        return color;
    }

    public Sign getSign() {
        return sign;
    }

    public boolean isNegative() {
        return sign == Sign.NEGATIVE;
    }

    public boolean isNeutral() { 
        return sign == Sign.NEUTRAL;
    }

    @Override
    public String toString() {
        if (isNeutral()) return "Potion{NEUTRALE}";
        return "Potion{color='" + color.toString() + "', sign='" + sign.toString() + "'}";
    }
}
