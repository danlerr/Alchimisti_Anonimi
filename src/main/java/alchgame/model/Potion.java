package alchgame.model;

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

    public Color  getColor()    { return color; }
    public Sign  getSign()     { return sign;  }
    public boolean isNegative()  { return "-".equals(sign); }

    @Override
    public String toString() {
        return "Potion{color='" + color.toString() + "', sign='" + sign.toString() + "'}";
    }
}
