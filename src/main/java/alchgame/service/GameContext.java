package com.alchemy.service;

import com.alchemy.service.Player;
import com.alchemy.service.Target;

import java.util.HashMap;
import java.util.Map;

/**
 * GameContext — fornisce accesso al Player corrente e ai Target della partita.
 */
public class GameContext {

    private final Player             currentPlayer;
    private final Map<String, Target> targets;

    public GameContext(Player currentPlayer, Map<String, Target> targets) {
        this.currentPlayer = currentPlayer;
        this.targets       = new HashMap<>(targets);
    }

    /** SD iniziaEsperimento — step 1.1 */
    public Target getTarget(String targetId) {
        Target t = targets.get(targetId);
        if (t == null) throw new IllegalArgumentException("Target non trovato: " + targetId);
        return t;
    }

    /** SD pagaOro / ConductExperiment — step 1.1 */
    public Player getCurrentPlayer() { return currentPlayer; }
}
