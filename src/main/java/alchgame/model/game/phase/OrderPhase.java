package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.board.Resources;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class OrderPhase extends Phase {

    public OrderPhase(Board board) {
        super(RoundPhase.ORDER, board);
    }

    @Override
    public List<Player> getPhaseOrder(List<Player> players, int startingPlayerIndex) {
        List<Player> normal = new ArrayList<>();
        List<Player> paralyzed = new ArrayList<>();
        int n = players.size();
        for (int i = 0; i < n; i++) {
            Player p = players.get((startingPlayerIndex + i) % n);
            if (p.isParalyzed()) paralyzed.add(p);
            else                 normal.add(p);
        }
        normal.addAll(paralyzed);
        return normal;
    }

    public List<String> getAvailableSlotIds() {
        return board.getAvailableSlotIds();
    }

    public Resources chooseSlot(Player current, String orderSlotID) {
        board.assignOrderSlot(orderSlotID, current);
        return board.assignSlotResources(orderSlotID, current);
    }

    public void onExit(List<Player> players) {
        players.forEach(Player::clearParalysis);
    }
}
