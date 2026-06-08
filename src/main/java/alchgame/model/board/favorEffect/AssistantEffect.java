package alchgame.model.board.favorEffect;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

public class AssistantEffect implements FavorEffect {

    private final int cubes;

    public AssistantEffect(int cubes) {
        this.cubes = cubes;
    }

    @Override
    public void apply(Player player, Board board) {
        player.addActionCubes(cubes);
    }
}
