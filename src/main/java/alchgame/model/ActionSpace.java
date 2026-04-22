package alchgame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ActionSpace — rappresenta uno spazio azione sul tabellone.
 * Ogni spazio ha un id e mantiene i player che lo hanno dichiarato.
 */
public class ActionSpace {

    private final String id;
    private final int maxCubes;
    private final List<Player> declaredPlayers = new ArrayList<>();

    public ActionSpace(String id) {
        this(id, Integer.MAX_VALUE);
    }

    public ActionSpace(String id, int maxCubes) {
        this.id = id;
        this.maxCubes = maxCubes;
    }

    public String getId() { return id; }

    public List<Player> getDeclaredPlayers() { return List.copyOf(declaredPlayers); }

    /**
     * Piazza il cubo azione del player su questo spazio.
     * Il player perde 1 cubo azione.
     */
    public void setAction(String actionSpaceId, Player player) {
        if (!this.id.equals(actionSpaceId))
            throw new IllegalArgumentException("Id spazio azione non corrispondente: " + actionSpaceId);
        if (declaredPlayers.size() >= maxCubes)
            throw new IllegalStateException("Spazio azione pieno: " + id);
        declaredPlayers.add(player);
        player.removeActionCube(1);
    }
}
