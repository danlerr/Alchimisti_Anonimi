package alchgame.application;

import alchgame.model.alchemy.Ingredient;
import alchgame.model.game.Round;
import alchgame.model.player.Player;
import alchgame.application.observer.*;

import java.util.function.Supplier;

/**
 * Controller del caso d'uso "tramutare un ingrediente" (UC04).
 */
public class TransmuteController extends Subject<ActionObserver> {

    private final Supplier<Round> round;
    private final int trasmuteGold;

    public TransmuteController(Supplier<Round> round, int trasmuteGold) {
        this.round = round;
        this.trasmuteGold = trasmuteGold;
    }

    public int transmuteIngredient(String ingredientId) {
        Player player = round.get().getCurrentPlayer();
        Ingredient selected = player.findIngredientById(ingredientId);
        player.removeIngredient(selected);
        player.addGold(this.trasmuteGold);
        notifyObservers(ActionObserver::onActionCompleted);
        return player.getGold();
    }
    
}
