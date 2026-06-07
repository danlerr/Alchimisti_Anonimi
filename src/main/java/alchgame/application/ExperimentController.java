package alchgame.application;

import alchgame.application.dto.assembler.DeductionGridAssembler;
import alchgame.application.dto.assembler.IngredientAssembler;
import alchgame.application.dto.assembler.PotionAssembler;
import alchgame.application.dto.DeductionGridDTO;
import alchgame.application.dto.IngredientDTO;
import alchgame.application.dto.PotionDTO;
import alchgame.application.observer.*;
import alchgame.model.alchemy.*;
import alchgame.model.alchemy.potionEffect.PotionEffectRegistry;
import alchgame.model.player.*;
import alchgame.model.game.*;

import java.util.List;
import java.util.Set;

public class ExperimentController extends Subject<ActionObserver> {

    private final AlchGame game;
    private final AlchemicAlgorithm alchemicAlgorithm;
    private final PotionEffectRegistry effectRegistry;

    public ExperimentController(AlchGame game, AlchemicAlgorithm alchemicAlgorithm, PotionEffectRegistry effectRegistry) {
        this.game = game;
        this.alchemicAlgorithm = alchemicAlgorithm;
        this.effectRegistry = effectRegistry;
    }

    public Set<String> getTargetIds() {
        return game.getTargets().keySet();
    }

    public boolean paymentCheck(String targetId) {
        return game.getTarget(targetId).requiresPayment();
    }

    public int payGold(String targetId) {
        Player player = game.getCurrentRound().getCurrentPlayer();
        Target target = game.getTarget(targetId);
        player.removeGold(target.getPaymentAmount());
        return player.getGold();
    }

    public List<IngredientDTO> getPlayerIngredients() {
        return game.getCurrentRound().getCurrentPlayer()
                .getIngredientsFromLab().stream()
                .map(IngredientAssembler::toDTO)
                .toList();
    }

    public PotionDTO conductExperiment(String targetId, String ingredientId1, String ingredientId2) {
        Player player = game.getCurrentRound().getCurrentPlayer();
        Ingredient i1 = player.findIngredientById(ingredientId1);
        Ingredient i2 = player.findIngredientById(ingredientId2);
        Target target = game.getTarget(targetId);
        Potion potion = alchemicAlgorithm.computePotion(i1, i2);
        player.updateLab(i1, i2, potion);
        player.publishExperimentResult(potion);
        target.applyEffect(potion, this.effectRegistry);
        notifyObservers(ActionObserver::onActionPerformed);
        return PotionAssembler.toDTO(potion);
    }

    /** Salta l'esperimento facendo comunque avanzare il turno di gioco. */
    public void skipExperiment() {
        notifyObservers(ActionObserver::onActionPerformed);
    }

    public DeductionGridDTO getDeductionGrid() {
        DeductionGrid grid = game.getCurrentRound().getCurrentPlayer().getDeductionGrid();
        return DeductionGridAssembler.toDTO(grid);
    }

    public void updateDeductionGrid(int ingredientIndex, int alchemicIndex) {
        Player player = game.getCurrentRound().getCurrentPlayer();
        DeductionGrid grid = player.getDeductionGrid();
        Ingredient ingredient = grid.getIngredients().get(ingredientIndex);
        AlchemicFormula formula = grid.getAlchemics().get(alchemicIndex);
        player.excludeFromDeductionGrid(ingredient, formula);
    }
}
