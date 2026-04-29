package alchgame.model.game;

import alchgame.model.board.Resources;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;

final class OrderPhase implements TurnPhase {

    @Override
    public TurnPhaseType type() {
        return TurnPhaseType.ORDER;
    }

    @Override
    public TurnPhase next() {
        return TurnPhases.declaration();
    }

    @Override
    public List<Player> getOrderPhaseOrder(GameSession game) {
        List<Player> normal = new ArrayList<>();
        List<Player> paralyzed = new ArrayList<>();
        List<Player> players = game.getPlayers();
        int n = players.size();

        for (int i = 0; i < n; i++) {
            Player player = players.get((game.getStartingPlayerIndex() + i) % n);
            if (player.isParalyzed()) {
                paralyzed.add(player);
                player.clearParalysis();
            } else {
                normal.add(player);
            }
        }

        normal.addAll(paralyzed);
        return normal;
    }

    @Override
    public Resources chooseSlot(GameSession game, String orderSlotID) {
        return game.getBoard().assignOrderSlot(orderSlotID, game.getCurrentPlayer());
    }
}
