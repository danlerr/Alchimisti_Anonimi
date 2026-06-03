package alchgame.application;

import alchgame.application.observer.*;
import alchgame.model.game.*;
import java.util.function.Supplier;

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
        // 1. Avanza il giocatore
        currentRound.nextPlayer();
        
        // 2. Controlla se la fase attuale è finita
        if (currentRound.isPhaseComplete()) {
            
            // 3. Passa alla fase successiva del round
            currentRound.nextPhase();
            
            // 4. Controlla se il passaggio di fase ha terminato il round
            if (currentRound.isOver()) {
                
                // Chiediamo al gioco se abbiamo raggiunto l'ultimo round
                if (alchgame.isOver()) { 
                    alchgame.calculateFinalScores();
                } else {
                    // Crea un nuovo round e lo imposta come corrente
                    alchgame.nextRound(); 
                }
            }
        }
    }

}