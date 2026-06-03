package alchgame.application.observer;

import java.util.ArrayList;
import java.util.List;


public abstract class ActionSubject {
    private final List<GameObserver> observers = new ArrayList<>();

    public void attach(GameObserver o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    public void detach(GameObserver o) {
        observers.remove(o);
    }

    protected void notifyObservers() {
        for (GameObserver o : observers) {
            o.update();
        }
    }
}