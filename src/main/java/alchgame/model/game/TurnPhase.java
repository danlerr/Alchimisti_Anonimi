package alchgame.model.game;

import alchgame.model.board.Resources;
import alchgame.model.player.Player;

import java.util.List;

public interface TurnPhase {

    TurnPhaseType type();
    TurnPhase next();

    default List<Player> getOrderPhaseOrder(GameSession game) {
        throw unsupported();
    }

    default Resources chooseSlot(GameSession game, String orderSlotID) {
        throw unsupported();
    }

    default List<Player> getDeclarationPhaseOrder(GameSession game) {
        throw unsupported();
    }

    default void declareAction(GameSession game, String actionSpaceId) {
        throw unsupported();
    }

    default List<Player> getResolutionOrderFor(GameSession game, String actionSpaceId) {
        throw unsupported();
    }

    default void endRound(GameSession game) {
        throw unsupported();
    }

    private IllegalStateException unsupported() {
        return new IllegalStateException("Operazione non ammessa durante la fase " + type() + ".");
    }
}
