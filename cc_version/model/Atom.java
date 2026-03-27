package alchgame.model;

public class Atom {
    private final String color;
    private final String symbol;
    private final String sign;

    public Atom(String color, String symbol, String sign) {
        this.color = color;
        this.symbol = symbol;
        this.sign = sign;
    }

    public String getColor() {
        return color;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSign() {
        return sign;
    }
}
