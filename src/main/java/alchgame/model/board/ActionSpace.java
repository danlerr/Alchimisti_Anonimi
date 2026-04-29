package alchgame.model.board;

import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * ActionSpace — represents an action space on the board.
 * Each space has an id and keeps track of the players that declared an action here.
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

    public void addDeclaredPlayer(Player player) {
        if (declaredPlayers.size() >= maxCubes)
            throw new IllegalStateException("Action space full: " + id);
        declaredPlayers.add(player);
    }

    public void reset() {
        declaredPlayers.clear();
    }
}
