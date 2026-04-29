package alchgame.model.game;

import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DeclarationPhase implements TurnPhase {

    @Override
    public TurnPhaseType type() {
        return TurnPhaseType.DECLARATION;
    }

    @Override
    public TurnPhase next() {
        return TurnPhases.resolution();
    }

    @Override
    public List<Player> getDeclarationPhaseOrder(GameSession game) {
        List<Player> wakeUp = new ArrayList<>(game.getBoard().getWakeUpOrder());
        Collections.reverse(wakeUp);
        return wakeUp;
    }

    @Override
    public void declareAction(GameSession game, String actionSpaceId) {
        game.getBoard().placeActionCube(actionSpaceId, game.getCurrentPlayer());
    }
}
