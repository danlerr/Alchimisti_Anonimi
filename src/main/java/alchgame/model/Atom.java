package alchgame.model;

public class Atom {

    private final String color;
    private final String size;
    private final String sign;

    public Atom(String color, String size, String sign) {
        this.color = color;
        this.size  = size;
        this.sign  = sign;
    }

    public String getColor() { return color; }
    public String getSize()  { return size;  }
    public String getSign()  { return sign;  }

    @Override
    public String toString() {
        return "Atom{color='" + color + "', size='" + size + "', sign='" + sign + "'}";
    }
}
