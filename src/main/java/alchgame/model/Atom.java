package alchgame.model;

public class Atom {

    private final Color color;
    private final Size size;
    private final Sign sign;

    public Atom(Color color, Size size, Sign sign) {
        this.color = color;
        this.size  = size;
        this.sign  = sign;
    }

    public Color getColor() { return color; }
    public Size getSize()   { return size;  }
    public Sign getSign()   { return sign;  }

    // Metodo di utilità per capire se l'atomo ha lo stesso segno di un altro
    public boolean hasSameSignAs(Atom other) {
        return this.sign == other.getSign();
    }

    // Metodo di utilità per capire se l'atomo ha la stessa dimensione di un altro
    public boolean hasSameSizeAs(Atom other) {
        return this.size == other.getSize();
    }

    @Override
    public String toString() {
        return "Atom{" +
                "color=" + color +
                ", size=" + size +
                ", sign=" + sign +
                '}';
    }
}
