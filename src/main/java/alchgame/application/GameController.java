package alchgame.application;

import alchgame.application.observer.*;
import alchgame.model.game.*;

/**
 * ConcreteObserver che fa avanzare il turno e le fasi.
 */
public class GameController extends Subject implements Observer {

    private final AlchGame alchgame;

    public GameController(AlchGame alchgame) {
        this.alchgame = alchgame;
    }

    /**
     * Questo metodo scatta automaticamente quando un controller chiama notifyObservers().
     */
    @Override
    public void update(){
        advanceDomain();
        notifyObservers();
    }

    private void advanceDomain() {
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