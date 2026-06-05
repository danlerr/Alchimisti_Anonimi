package alchgame.presentation;

import alchgame.model.player.Player;

import java.util.Map;
import java.util.function.Consumer;

public class ActionDispatcher {

    private final Map<String, Consumer<Player>> handlers;

    public ActionDispatcher(Map<String, Consumer<Player>> handlers) {
        this.handlers = Map.copyOf(handlers);
    }

    public void dispatch(String actionId, Player player) {
        Consumer<Player> handler = handlers.get(actionId);
        if (handler != null) handler.accept(player);
    }

    public boolean supports(String actionId) {
        return handlers.containsKey(actionId);
    }
}
