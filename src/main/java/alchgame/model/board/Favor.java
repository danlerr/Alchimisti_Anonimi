package alchgame.model.board;

import alchgame.model.board.favorEffect.FavorEffectStrategy;
import alchgame.model.player.Player;

public class Favor {

    private final String name;
    private final FavorEffectStrategy effect;

    public Favor(String name, FavorEffectStrategy effect) {
        this.name = name;
        this.effect = effect;
    }

    public String getName() { return name; }

    public void activate(Player player, Board board) {
        effect.apply(player, board);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Favor other)) return false;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "FavorCard{name='" + name + "'}";
    }
}
