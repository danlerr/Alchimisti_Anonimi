package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.game.TurnPhase;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class OrderState implements TurnState {

    @Override
    public TurnPhase getPhase() {
        return TurnPhase.ORDER;
    }

    @Override
    public List<Player> getPhaseOrder(List<Player> players, int startingPlayerIndex, Board board) {
        List<Player> normal    = new ArrayList<>();
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

    @Override
    public TurnState advance(List<Player> players, Board board) {
        players.forEach(Player::clearParalysis);
        return new DeclarationState();
    }
}
