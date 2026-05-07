package alchgame.controller;

import alchgame.model.game.Round;

import java.util.function.Supplier;

public class ForageController implements ActionController {

    private final Supplier<Round> round;

    public ForageController(Supplier<Round> round) {
        this.round = round;
    }
}
