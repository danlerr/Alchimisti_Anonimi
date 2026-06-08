package alchgame.application;

import alchgame.model.alchemy.Ingredient;
import alchgame.model.game.Round;
import alchgame.model.player.Player;
import alchgame.application.dto.IngredientDTO;
import alchgame.application.dto.assembler.IngredientAssembler;
import alchgame.application.observer.*;

import java.util.ArrayList;
import java.util.List;
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

    public IngredientDTO forageIngredient() {
        Round r = round.get();
        Player player = r.getCurrentPlayer();
        List<Ingredient> before = new ArrayList<>(player.getIngredientsFromLab());
        r.getBoard().dealIngredients(player, this.forageYield);
        Ingredient received = player.getIngredientsFromLab().stream()
                .filter(i -> !before.contains(i))
                .findFirst().orElseThrow();
        return IngredientAssembler.toDTO(received);
    }

    public void endForage() {
        notifyObservers(ActionObserver::onActionPerformed);
    }
}
