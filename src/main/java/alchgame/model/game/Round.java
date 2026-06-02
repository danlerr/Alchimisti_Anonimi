package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestisce l'intero flusso di un Round di gioco.
 * una macchina a stati basata su Enum, che per ogni fase genera la "coda" 
 * dei giocatori che devono agire e la scorre tramite un cursore.
 */
public class Round {

    public enum Phase { ORDER, DECLARATION, RESOLUTION, ENDED }

    private Phase currentPhase;
    private final Board board;
    private final List<Player> players;
    private final int startingPlayerIndex;

    private List<Player> turnQueue;
    private int cursor;
    public record ResolutionStep(String actionId, Player player) {}
    private List<ResolutionStep> resolutionQueue;

    Round(Board board, List<Player> players, int startingPlayerIndex) {
        this.board = board;
        this.players = players;
        this.startingPlayerIndex = startingPlayerIndex;
        startOrderPhase();
    }

    // // ------------------------------------------------------------------------
    // // --- ACCESSO AI DATI (Per i Presenter/View)
    // // ------------------------------------------------------------------------

    // public Phase getPhase() { return currentPhase; }
    public boolean isRoundOver() { return currentPhase == Phase.ENDED; }
    public Board getBoard() { return board; }

    /** Ritorna il giocatore di turno (valido per Order e Declaration) */
    public Player getCurrentPlayer() {
        if (isRoundOver()) return null;
        if (currentPhase == Phase.RESOLUTION) return resolutionQueue.get(cursor).player();
        return turnQueue.get(cursor);
    }

    // /** Ritorna l'azione attualmente in risoluzione (valido SOLO per ResolutionPhase) */
    // public String getCurrentResolutionActionId() {
    //     if (currentPhase != Phase.RESOLUTION) throw new IllegalStateException("Non siamo in fase di Risoluzione");
    //     return resolutionQueue.get(cursor).actionId();
    // }

    public void nextPlayer() {
        cursor++;
        boolean phaseComplete = (currentPhase == Phase.RESOLUTION) ? 
                                (cursor >= resolutionQueue.size()) : 
                                (cursor >= turnQueue.size());

        if (phaseComplete) {
            nextPhase();
        }
    }

    // DA VALUTARE QUESTO SWITCH!!!
    private void nextPhase() {
        switch (currentPhase) {
            case ORDER -> startDeclarationPhase();
            case DECLARATION -> startResolutionPhase();
            case RESOLUTION -> this.currentPhase = Phase.ENDED;
            case ENDED -> {} 
        }
    }

    private void startOrderPhase() {
        this.currentPhase = Phase.ORDER;
        this.cursor = 0;
        this.turnQueue = new ArrayList<>();
        
        List<Player> paralyzed = new ArrayList<>();
        int n = players.size();
        for (int i = 0; i < n; i++) {
            Player p = players.get((startingPlayerIndex + i) % n);
            if (p.isParalyzed()) paralyzed.add(p); else turnQueue.add(p);
        }
        turnQueue.addAll(paralyzed);
    }

    private void startDeclarationPhase() {

        players.forEach(Player::clearParalysis);

        this.currentPhase = Phase.DECLARATION;
        this.cursor = 0;
        this.turnQueue = new ArrayList<>(board.getWakeUpOrder());
        Collections.reverse(this.turnQueue);
    }

    private void startResolutionPhase() {
        this.currentPhase = Phase.RESOLUTION;
        this.cursor = 0;
        this.resolutionQueue = new ArrayList<>();

        List<Player> wakeUpOrder = board.getWakeUpOrder();
        
        for (String actionId : board.getActionSpaceIds()) {
            List<Player> declaredPlayersInSpace = board.getActionSpace(actionId).getDeclaredPlayers();
            
            for (Player player : wakeUpOrder) {
                long cubesPlaced = declaredPlayersInSpace.stream().filter(player::equals).count();
                for (int i = 0; i < cubesPlaced; i++) {
                    resolutionQueue.add(new ResolutionStep(actionId, player));
                }
            }
        }
    }
}