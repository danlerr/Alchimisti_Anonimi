package alchgame.model;

public class Potion {
    private final String color;
    private final String sign;

    public Potion(String color, String sign) {
        this.color = color;
        this.sign = sign;
    }

    public String getColor() {
        return color;
    }

    public String getSign() {
        return sign;
    }
}
