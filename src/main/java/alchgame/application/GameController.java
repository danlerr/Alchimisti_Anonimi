package alchgame.application;

import alchgame.application.observer.*;
import alchgame.model.game.*;

/**
 * ConcreteObserver che fa avanzare il turno e le fasi.
 */
public class GameController implements GameObserver {

    private final AlchGame alchgame;
    //osservatori del presentation
    // private final List <GameObserver> observers = new ArrayList<>();

    public GameController(AlchGame alchgame) {
        this.alchgame = alchgame;
    }

    /**
     * Questo metodo scatta automaticamente quando un controller chiama notifyObservers().
     */
    @Override
    public void update() {
        Round currentRound = alchgame.getCurrentRound();
        currentRound.nextPlayer();

        if (currentRound.isPhaseComplete()) {
            currentRound.nextPhase();

            if (currentRound.isOver()) {
                
                if (alchgame.isOver()) { 
                    alchgame.calculateFinalScores();
                } else {
                    alchgame.nextRound(); 
                }
            }
        }
    }

}