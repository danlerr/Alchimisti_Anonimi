package alchgame.application.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Subject<T> {
    private final List<T> observers = new ArrayList<>();

    public void attach(T o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    public void detach(T o) {
        observers.remove(o);
    }

    protected void notifyObservers(Consumer<T> call) {
        observers.forEach(call);
    }
}
