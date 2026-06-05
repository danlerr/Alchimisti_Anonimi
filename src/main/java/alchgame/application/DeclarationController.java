package alchgame.application;

import alchgame.model.board.Board;
import alchgame.model.game.Round;
import alchgame.application.observer.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * Controller del caso d'uso "dichiara azioni" (UC02).
 */
public class DeclarationController extends Subject<ActionObserver> {

    private final Supplier<Round> round;

    public DeclarationController(Supplier<Round> round, Board board) {
        this.round = round;
    }

    public List<String> getActionList() {
        return round.get().getBoard().getActionSpaceIds();
    }

    /**
     * Comando: piazza un cubo azione e notifica gli observer ({@code onActionPerformed}),
     * che fanno emettere un evento di refresh dello stato corrente. Non avanza il
     * turno: l'avanzamento è responsabilità del {@code GameController}.
     */
    public void declareAction(String actionSpaceId) {
        round.get().getBoard().placeActionCube(actionSpaceId, round.get().getCurrentPlayer());
        notifyObservers(ActionObserver::onActionPerformed);
    }
}
