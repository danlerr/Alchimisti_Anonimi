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
    
    /**
     * Checks if this potion has a positive sign.
     * @return true if sign is "+", false otherwise
     */
    public boolean isPositive() {
        return "+".equals(sign);
    }
    
    /**
     * Checks if this potion has a negative sign.
     * @return true if sign is "-", false otherwise
     */
    public boolean isNegative() {
        return "-".equals(sign);
    }
    
    /**
     * Checks if this potion is neutral (no effect).
     * @return true if sign is "0" or "neutral", false otherwise
     */
    public boolean isNeutral() {
        return "0".equals(sign) || "neutral".equals(color);
    }
}
