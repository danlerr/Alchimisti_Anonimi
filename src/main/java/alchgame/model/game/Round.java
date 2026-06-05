package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.game.phase.OrderPhase;
import alchgame.model.game.phase.Phase;
import alchgame.model.player.Player;

import java.util.List;

/**
 * Contesto del pattern State: delega tutto alla fase corrente.
 * Gestisce anche i Target (bersagli) disponibili per il round in corso,
 * unendo i bersagli statici (es. Studente) al giocatore di turno.
 */
public class Round {

    private Phase currentPhase;
    private final Board board;

    Round(Board board, List<Player> players, int startingPlayerIndex) {
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

    public boolean isOver() {
        return currentPhase == null;
    }
    
}