package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.game.phase.OrderPhase;
import alchgame.model.game.phase.Phase;
import alchgame.model.player.Player;

import java.util.List;
import java.util.Map;

/**
 * Contesto del pattern State: delega tutto alla fase corrente.
 * Gestisce anche i Target (bersagli) disponibili per il round in corso,
 * unendo i bersagli statici (es. Studente) al giocatore di turno.
 */
public class Round {

    private Phase currentPhase;
    private final Board board;

    Round(Board board, List<Player> players, int startingPlayerIndex, 
          Map<String, Target> staticTargets, String selfId) {
        this.board = board;
        this.currentPhase = new OrderPhase(board, players, startingPlayerIndex);
    }

    public void nextPhase() {
        if (currentPhase == null || !currentPhase.isComplete()) return;
        
        currentPhase = currentPhase.next().orElse(null); 
    }

    public void nextPlayer(){
        if (currentPhase != null) {
            currentPhase.nextPlayer(); 
        }
    }

    public Phase getCurrentPhase() { 
        return currentPhase; 
    }

    public boolean isPhaseComplete() {
        return currentPhase == null || currentPhase.isComplete(); 
    }

    public Player getCurrentPlayer() {
        return currentPhase != null ? currentPhase.getCurrentPlayer() : null; 
    }

    public Board getBoard() {
         return board; 
    }

    public Map<String, Target> getTargets() {
        Map<String, Target> availableTargets = new LinkedHashMap<>();
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            availableTargets.put(selfId, currentPlayer);
        }
        availableTargets.putAll(staticTargets);
        
        return Collections.unmodifiableMap(availableTargets);
    }

    public Target getTarget(String targetId) {
        Target target = getTargets().get(targetId);
        if (target == null) {
            throw new IllegalArgumentException("Target non valido: " + targetId);
        }
        return target;
    }

    public boolean isOver() {
        return currentPhase == null;
    }
    
}