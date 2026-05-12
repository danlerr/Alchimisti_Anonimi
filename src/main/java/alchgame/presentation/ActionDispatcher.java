package alchgame.presentation;

import java.util.Map;

public class ActionDispatcher {

    private final Map<String, Runnable> handlers;

    public ActionDispatcher(Map<String, Runnable> handlers) {
        this.handlers = Map.copyOf(handlers);
    }

    public void dispatch(String actionId) {
        Runnable handler = handlers.get(actionId);
        if (handler != null) handler.run();
    }

    public boolean supports(String actionId) {
        return handlers.containsKey(actionId);
    }
}
