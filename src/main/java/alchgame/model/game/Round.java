package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.game.phase.OrderPhase;
import alchgame.model.game.phase.Phase;
import alchgame.model.player.Player;

import java.util.List;

/**
 * Contesto del pattern State: delega tutto alla fase corrente.
 * La sequenza delle fasi è codificata nella catena Phase.next(); Round non la conosce.
 * Gli accessor orderPhase/declarationPhase/resolutionPhase proteggono con instanceof:
 * chiamarli fuori dalla fase corretta lancia IllegalStateException.
 */
public class Round {

    private Phase currentPhase;
    private final Board board;

    Round(Board board, List<Player> players, int startingPlayerIndex) {
        this.board = board;
        this.currentPhase = new OrderPhase(board, players, startingPlayerIndex);
    }

    public void nextPhase() {
        if (!currentPhase.isComplete()) return;
        currentPhase = currentPhase.next()
                .orElseThrow(() -> new IllegalStateException("Il Round è già terminato."));
    }

    public Phase getCurrentPhase() { 
        return currentPhase; 
    }

    public boolean isPhaseComplete() {
        return currentPhase.isComplete(); 
    }

    public void nextPlayer(){
        currentPhase.nextPlayer(); 
    }

    public Player getCurrentPlayer() {
        return currentPhase.getCurrentPlayer(); 
    }

    public Board getBoard() {
         return board; 
    }
}
