package alchgame.model.board;

import java.util.ArrayDeque;
import java.util.Deque;

public class CardDeck<T> implements Deck<T> {

    private final Deque<T> cards;

    public CardDeck(Deque<T> cards) {
        this.cards = new ArrayDeque<>(cards);
    }

    @Override
    public T draw() {
        T card = cards.poll();
        if (card == null)
            throw new IllegalStateException("Deck exhausted.");
        return card;
    }

    @Override
    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
