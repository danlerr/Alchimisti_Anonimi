package alchgame.application;

import alchgame.application.dto.assembler.GameStateAssembler;
import alchgame.application.dto.GameStateDTO;
import alchgame.application.observer.*;
import alchgame.model.game.*;
import alchgame.model.game.phase.Phase;

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
        GameTransition transition = alchgame.advance();
        return GameStateAssembler.assemble(transition, alchgame.getBoard(), alchgame.getPlayers());
    }
}
