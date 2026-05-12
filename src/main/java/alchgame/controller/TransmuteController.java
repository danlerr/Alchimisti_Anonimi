package alchgame.controller;

import alchgame.model.game.Round;
import java.util.function.Supplier;

public class TransmuteController {

    private final Supplier<Round> round;

    public TransmuteController(Supplier<Round> round) {
        this.round = round;
    }
}
