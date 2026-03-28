package alchgame.service;

import alchgame.model.Player;
import alchgame.model.Target;

import java.util.HashMap;
import java.util.Map;

/**
 * GameContext — fornisce accesso ai dati della partita.
 */
public class GameContext {

    private final Player currentPlayer;    //player del turno (cambia ogni turno)
    private final Map<String, Target> targets;     

    public GameContext(Player currentPlayer, Map<String, Target> targets) {
        this.currentPlayer = currentPlayer;
        this.targets = new HashMap<>(targets);
    }

    public Target getTarget(String targetId) {
        Target t = targets.get(targetId);
        if (t == null) throw new IllegalArgumentException("Target non trovato: " + targetId);
        return t;
    }
    
    public Player getCurrentPlayer() { return currentPlayer; }
}
