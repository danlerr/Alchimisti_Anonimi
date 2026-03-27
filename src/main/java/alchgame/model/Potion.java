package alchgame.model;

public class Potion {

    private final String color;
    private final String sign; // "+" positiva | "-" negativa

    private Potion(String color, String sign) {
        this.color = color;
        this.sign  = sign;
    }

    public static Potion createPotion(String color, String sign) {
        return new Potion(color, sign);
    }

    public String  getColor()    { return color; }
    public String  getSign()     { return sign;  }
    public boolean isNegative()  { return "-".equals(sign); }

    @Override
    public String toString() {
        return "Potion{color='" + color + "', sign='" + sign + "'}";
    }
}
