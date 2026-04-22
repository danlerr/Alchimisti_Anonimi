package alchgame.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Board — tabellone di gioco. Contiene gli spazi azione indicizzati per id.
 */
public class Board {

    private final Map<String, ActionSpace> actionSpaces;

    public Board(Map<String, ActionSpace> actionSpaces) {
        this.actionSpaces = new HashMap<>(actionSpaces);
    }

    public ActionSpace getActionSpace(String actionSpaceId) {
        ActionSpace space = actionSpaces.get(actionSpaceId);
        if (space == null)
            throw new IllegalArgumentException("Spazio azione non trovato: " + actionSpaceId);
        return space;
    }

    /**
     * Delega la dichiarazione dell'azione allo spazio azione corrispondente.
     */
    public void setAction(String actionSpaceId, Player player) {
        getActionSpace(actionSpaceId).setAction(actionSpaceId, player);
    }
}
