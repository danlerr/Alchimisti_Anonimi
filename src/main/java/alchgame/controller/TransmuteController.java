package alchgame.controller;

import alchgame.model.game.Round;

import java.util.function.Supplier;

public class TransmuteController implements ActionController {

    private final Supplier<Round> round;

    public TransmuteController(Supplier<Round> round) {
        this.round = round;
    }

    public void execute() {
        // stub: trasmutazione non ancora implementata
    }
}
