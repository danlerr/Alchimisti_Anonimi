package alchgame.controller;

import alchgame.controller.action.ActionController;

import java.util.Map;

public class ActionResolverRegistry {

    private final Map<String, ActionController> byActionId;

    public ActionResolverRegistry(Map<String, ActionController> byActionId) {
        this.byActionId = Map.copyOf(byActionId);
    }

    public ActionController get(String actionId) {
        ActionController controller = byActionId.get(actionId);
        if (controller == null) {
            throw new IllegalArgumentException("Nessun controller registrato per l'azione: " + actionId);
        }
        return controller;
    }

    public boolean contains(String actionId) {
        return byActionId.containsKey(actionId);
    }
}
