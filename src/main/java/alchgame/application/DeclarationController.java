package alchgame.application;

import alchgame.model.game.Round;
import alchgame.model.player.Player;
import alchgame.application.observer.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * Controller del caso d'uso "dichiara azioni" (UC02).
 */
public class DeclarationController extends Subject<ActionObserver> {

    private final Supplier<Round> round;

    public DeclarationController(Supplier<Round> round) {
        this.round = round;
    }

    public List<String> getActionList() {
        return round.get().getBoard().getActionSpaceIds();
    }

    public void declareAction(String actionSpaceId) {
        round.get().getBoard().placeActionCube(actionSpaceId, round.get().getCurrentPlayer());
    }

    public void pass() {
        Player player = round.get().getCurrentPlayer();
        player.removeActionCube(player.getActionCubes());
    }

    public void endDeclaration() {
        notifyObservers(ActionObserver::onActionPerformed);
    }
}
