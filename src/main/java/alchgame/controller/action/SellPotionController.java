package alchgame.controller.action;

import alchgame.model.game.Round;

import java.util.function.Supplier;

public class SellPotionController implements ActionController {

    private final Supplier<Round> round;

    public SellPotionController(Supplier<Round> round) {
        this.round = round;
    }
}
