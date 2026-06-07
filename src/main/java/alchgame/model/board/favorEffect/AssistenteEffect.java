package alchgame.model.board.favorEffect;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

public class AssistenteEffect implements FavorEffectStrategy {

    private final int cubes;

    public AssistenteEffect(int cubes) {
        this.cubes = cubes;
    }

    @Override
    public void apply(Player player, Board board) {
        player.addActionCubes(cubes);
    }
}
