package alchgame.application;

import alchgame.application.dto.assembler.IngredientAssembler;
import alchgame.application.dto.IngredientDTO;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.game.Round;
import alchgame.model.player.Player;
import alchgame.application.observer.*;

import java.util.List;
import java.util.function.Supplier;

/**
 * Controller del caso d'uso "tramutare un ingrediente" (UC04).
 */
public class TransmuteController extends Subject<ActionObserver> {

    private final Supplier<Round> round;
    private final int transmuteGold;

    public TransmuteController(Supplier<Round> round, int trasmuteGold) {
        this.round = round;
        this.transmuteGold = trasmuteGold;
    }

    public List<IngredientDTO> getPlayerIngredients() {
        return IngredientAssembler.toDTOList(
            round.get().getCurrentPlayer().getIngredientsFromLab()
        );
    }

    public int transmuteIngredient(String ingredientId) {
        Player player = round.get().getCurrentPlayer();
        Ingredient selected = player.findIngredientById(ingredientId);
        player.removeIngredient(selected);
        player.addGold(this.transmuteGold);
        return player.getGold();
    }

    public void endTransmute(){
        notifyObservers(ActionObserver::onActionPerformed);
    }
}
