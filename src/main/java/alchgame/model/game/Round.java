package alchgame.model.game;

import alchgame.model.board.Board;
import alchgame.model.game.phase.DeclarationPhase;
import alchgame.model.game.phase.OrderPhase;
import alchgame.model.game.phase.Phase;
import alchgame.model.game.phase.ResolutionPhase;
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

    public Player getCurrentPlayer() {
        return currentPhase.getCurrentPlayer(); 
    }

    public boolean isPhaseComplete() {
        return currentPhase.isComplete(); 
    }

    public void advanceTurn(){
        currentPhase.advanceTurn(); 
    }

    public Phase currentPhase() { 
        return currentPhase; 
    }

    public void tryAdvancePhase() {
        if (!currentPhase.isComplete()) return;
        currentPhase = currentPhase.next()
                .orElseThrow(() -> new IllegalStateException("Il Round è già terminato."));
    }

    public OrderPhase orderPhase() {
        if (!(currentPhase instanceof OrderPhase op))
            throw new IllegalStateException("Operazione ammessa solo durante ORDER.");
        return op;
    }

    public DeclarationPhase declarationPhase() {
        if (!(currentPhase instanceof DeclarationPhase dp))
            throw new IllegalStateException("Operazione ammessa solo durante DECLARATION.");
        return dp;
    }

    public ResolutionPhase resolutionPhase() {
        if (!(currentPhase instanceof ResolutionPhase rp))
            throw new IllegalStateException("Operazione ammessa solo durante RESOLUTION.");
        return rp;
    }

    public Board getBoard() {
         return board; 
    }
}
