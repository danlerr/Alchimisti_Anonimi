package alchgame.application;

import alchgame.application.dto.assembler.GameStateAssembler;
import alchgame.application.dto.GameStateDTO;
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

    @Override
    public void onActionPerformed() {
        notifyObservers(o -> o.onGameEvent(nextGameState()));
    }

    public int getTotalRounds() {
        return alchgame.getTotalRounds();
    }

    public GameStateDTO getInitialState() {
        Phase phase = alchgame.getCurrentRound().getCurrentPhase();
        return GameStateAssembler.phaseChanged(phase, alchgame.getCurrentRoundNumber(), alchgame.getBoard(), alchgame.getPlayers());
    }

    private GameStateDTO nextGameState() {
        Round currentRound = alchgame.getCurrentRound();
        Phase phase = currentRound.getCurrentPhase();
        int roundNumber = alchgame.getCurrentRoundNumber();

        if (phase.retainsTurn()) {
            return GameStateAssembler.turnAdvanced(phase, roundNumber, alchgame.getBoard(), alchgame.getPlayers());
        }

        currentRound.nextPlayer();

        if (currentRound.isPhaseComplete()) {
            currentRound.nextPhase();
            // salta eventuali fasi già complete al momento della transizione (es. ResolutionPhase vuota)
            while (!currentRound.isOver() && currentRound.isPhaseComplete()) {
                currentRound.nextPhase();
            }
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
            return GameStateAssembler.phaseChanged(newPhase, alchgame.getCurrentRoundNumber(), alchgame.getBoard(), alchgame.getPlayers());
        }

        return GameStateAssembler.turnAdvanced(currentRound.getCurrentPhase(), roundNumber, alchgame.getBoard(), alchgame.getPlayers());
    }
}
