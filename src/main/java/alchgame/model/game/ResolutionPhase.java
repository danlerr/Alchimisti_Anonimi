package alchgame.model.game;

import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;

final class ResolutionPhase implements TurnPhase {

    @Override
    public TurnPhaseType type() {
        return TurnPhaseType.RESOLUTION;
    }

    @Override
    public TurnPhase next() {
        return TurnPhases.cleanup();
    }

    @Override
    public List<Player> getResolutionOrderFor(GameSession game, String actionSpaceId) {
        List<Player> declared = game.getBoard().getActionSpace(actionSpaceId).getDeclaredPlayers();
        List<Player> resolutionOrder = new ArrayList<>();

        for (Player player : game.getBoard().getWakeUpOrder()) {
            for (Player declaredPlayer : declared) {
                if (declaredPlayer.equals(player))
                    resolutionOrder.add(player);
            }
        }

        return resolutionOrder;
    }
}
