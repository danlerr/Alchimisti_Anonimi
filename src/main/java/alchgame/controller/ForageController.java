package alchgame.controller;

import alchgame.model.game.Round;
import alchgame.model.player.Player;

import java.util.function.Supplier;

public class ForageController {

    private final Supplier<Round> round;

    public ForageController(Supplier<Round> round) {
        this.round = round;
    }

    public void forageIngredient() {
        Round r = round.get();
        Player player = r.getCurrentPlayer();

        r.getBoard().dealIngredients(player, 1);
    }
}
