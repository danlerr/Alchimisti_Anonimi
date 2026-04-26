package alchgame.model;

public interface Deck<T> {

    T draw();

    boolean isEmpty();
}
