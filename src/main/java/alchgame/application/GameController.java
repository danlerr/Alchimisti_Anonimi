package alchgame.application;

import alchgame.application.observer.*;
import alchgame.model.game.*;

/**
 * ConcreteObserver che fa avanzare il turno e le fasi.
 */
public class GameController extends Subject<GameObserver> implements ActionObserver {

    private final AlchGame alchgame;

    public GameController(AlchGame alchgame) {
        this.alchgame = alchgame;
    }

    /**
     * Questo metodo scatta automaticamente quando un controller chiama notifyObservers().
     */
    @Override
    public void onActionCompleted() {
        GameEvent event = advanceDomain();
        notifyObservers(o -> o.onGameEvent(event));
    }

    private GameEvent advanceDomain() {
        Round currentRound = alchgame.getCurrentRound();
        currentRound.nextPlayer();

        if (currentRound.isPhaseComplete()) {
            currentRound.nextPhase();
            if (currentRound.isOver()) {
                if (alchgame.isOver()) {
                    alchgame.calculateFinalScores();
                    return GameEvent.GAME_OVER;
                } else {
                    alchgame.nextRound();
                    return GameEvent.ROUND_ENDED;
                }
            }
            return GameEvent.PHASE_CHANGED;
        }
        return GameEvent.TURN_ADVANCED;
    }
}