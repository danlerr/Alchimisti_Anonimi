package alchgame.application;

import alchgame.application.observer.*;
import alchgame.model.game.*;
import alchgame.model.game.phase.*;
import alchgame.model.player.Player;

import java.util.List;

public class GameController extends Subject<GameObserver> implements ActionObserver {

    private final AlchGame alchgame;

    public GameController(AlchGame alchgame) {
        this.alchgame = alchgame;
    }

    @Override
    public void onActionCompleted() {
        GameStateDTO state = advanceDomain();
        notifyObservers(o -> o.onGameEvent(state));
    }

    public GameStateDTO getInitialState() {
        Phase phase = alchgame.getCurrentRound().getCurrentPhase();
        return new GameStateDTO(
            GameStateDTO.EventType.PHASE_CHANGED,
            phaseTypeOf(phase),
            phase.getCurrentPlayer(),
            actionIdOf(phase),
            alchgame.getCurrentRoundNumber(),
            null
        );
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
                    return new GameStateDTO(
                        GameStateDTO.EventType.GAME_OVER,
                        null, null, null,
                        roundNumber,
                        ranking
                    );
                } else {
                    alchgame.nextRound();
                    return new GameStateDTO(
                        GameStateDTO.EventType.ROUND_ENDED,
                        null, null, null,
                        roundNumber,
                        null
                    );
                }
            }
            Phase newPhase = alchgame.getCurrentRound().getCurrentPhase();
            return new GameStateDTO(
                GameStateDTO.EventType.PHASE_CHANGED,
                phaseTypeOf(newPhase),
                newPhase.getCurrentPlayer(),
                actionIdOf(newPhase),
                alchgame.getCurrentRoundNumber(),
                null
            );
        }

        Phase phase = alchgame.getCurrentRound().getCurrentPhase();
        return new GameStateDTO(
            GameStateDTO.EventType.TURN_ADVANCED,
            phaseTypeOf(phase),
            phase.getCurrentPlayer(),
            actionIdOf(phase),
            roundNumber,
            null
        );
    }

    private GameStateDTO.PhaseType phaseTypeOf(Phase phase) {
        if (phase instanceof OrderPhase)       return GameStateDTO.PhaseType.ORDER;
        if (phase instanceof DeclarationPhase) return GameStateDTO.PhaseType.DECLARATION;
        if (phase instanceof ResolutionPhase)  return GameStateDTO.PhaseType.RESOLUTION;
        return null;
    }

    private String actionIdOf(Phase phase) {
        if (phase instanceof ResolutionPhase rp) return rp.getCurrentActionId();
        return null;
    }
}
