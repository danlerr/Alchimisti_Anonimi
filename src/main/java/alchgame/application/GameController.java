package alchgame.application;

import alchgame.application.assembler.GameStateAssembler;
import alchgame.application.observer.*;
import alchgame.model.game.*;
import alchgame.model.game.phase.Phase;
import alchgame.model.player.Player;

import java.util.List;

public class GameController extends Subject<GameObserver> implements ActionObserver {

    private final AlchGame alchgame;

    public GameController(AlchGame alchgame) {
        this.alchgame = alchgame;
    }

    /**
     * Un'azione ha mutato lo stato: emette un evento di refresh dello stato
     * corrente (stesso giocatore, nessun avanzamento).
     */
    @Override
    public void onActionPerformed() {
        Round round = alchgame.getCurrentRound();
        GameStateDTO state = GameStateAssembler.turnRefreshed(
                round.getCurrentPhase(), alchgame.getCurrentRoundNumber(), alchgame.getBoard());
        notifyObservers(o -> o.onGameEvent(state));
    }

    /**
     * Avanzamento esplicito del turno, invocato dai presenter quando il turno
     * corrente è concluso (passa, oppure azione singola completata).
     */
    public void endTurn() {
        GameStateDTO state = advanceDomain();
        notifyObservers(o -> o.onGameEvent(state));
    }

    public int getTotalRounds() {
        return alchgame.getTotalRounds();
    }

    public GameStateDTO getInitialState() {
        Phase phase = alchgame.getCurrentRound().getCurrentPhase();
        return GameStateAssembler.phaseChanged(phase, alchgame.getCurrentRoundNumber(), alchgame.getBoard());
    }

    private GameStateDTO advanceDomain() {
        Round currentRound = alchgame.getCurrentRound();
        int roundNumber = alchgame.getCurrentRoundNumber();
        currentRound.nextPlayer();

        if (currentRound.isPhaseComplete()) {
            currentRound.nextPhase();
            if (currentRound.isOver()) {
                if (alchgame.isOver()) {
                    List<Player> ranking = alchgame.calculateFinalScores();
                    return GameStateAssembler.gameOver(ranking, roundNumber);
                } else {
                    alchgame.nextRound();
                    return GameStateAssembler.roundEnded(roundNumber);
                }
            }
            Phase newPhase = alchgame.getCurrentRound().getCurrentPhase();
            return GameStateAssembler.phaseChanged(newPhase, alchgame.getCurrentRoundNumber(), alchgame.getBoard());
        }

        Phase phase = alchgame.getCurrentRound().getCurrentPhase();
        return GameStateAssembler.turnAdvanced(phase, roundNumber, alchgame.getBoard());
    }
}
