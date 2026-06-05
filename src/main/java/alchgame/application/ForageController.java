package alchgame.application;

import alchgame.model.game.Round;
import alchgame.model.player.Player;
import alchgame.application.observer.*;
import java.util.function.Supplier;

/**
 * Controller del caso d'uso "ricerca un ingrediente" (UC03).
 */
public class ForageController extends Subject<ActionObserver> {

    private final Supplier<Round> round;
    private final int forageYield;

    public ForageController(Supplier<Round> round, int forageYield) {
        this.round = round;
        this.forageYield = forageYield;
    }

    public void forageIngredient() {
        Round r = round.get();
        Player player = r.getCurrentPlayer();

        r.getBoard().dealIngredients(player, this.forageYield);

        notifyObservers(ActionObserver::onActionPerformed);
    }
}
