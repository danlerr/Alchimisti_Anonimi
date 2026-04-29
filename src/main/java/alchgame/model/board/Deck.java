package alchgame.model.board;

public interface Deck<T> {

    T draw();

    boolean isEmpty();
}
