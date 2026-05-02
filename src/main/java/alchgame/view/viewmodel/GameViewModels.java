package alchgame.view.viewmodel;

import alchgame.GameConfig;
import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.alchemy.Atom;
import alchgame.model.player.DeductionGrid;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.Potion;
import alchgame.model.player.PrivateLaboratory;
import alchgame.model.board.Resources;
import alchgame.model.alchemy.Sign;
import alchgame.model.alchemy.Size;
import alchgame.model.game.StudentState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GameViewModels {

    private GameViewModels() {
    }

    public static List<String> playerNames(List<? extends alchgame.model.player.Player> players) {
        return players.stream()
            .map(alchgame.model.player.Player::getName)
            .toList();
    }

    public static List<String> ingredientNames(List<Ingredient> ingredients) {
        return ingredients.stream()
            .map(Ingredient::getName)
            .toList();
    }

    public static List<OrderSlotView> orderSlots(List<String> availableSlotIds, List<GameConfig.SlotSpec> allSlots) {
        return availableSlotIds.stream()
            .map(slotId -> new OrderSlotView(slotId, resourcesFor(slotId, allSlots)))
            .toList();
    }

    public static SlotAssignmentView slotAssignment(String playerName, String slotId, Resources resources) {
        return new SlotAssignmentView(playerName, slotId, resourceGain(resources));
    }

    public static ExperimentResultView experimentResult(Ingredient first, Ingredient second, Potion potion) {
        return new ExperimentResultView(first.getName(), second.getName(), potion(potion));
    }

    public static ExperimentResultView experimentResult(String firstName, String secondName, Potion potion) {
        return new ExperimentResultView(firstName, secondName, potion(potion));
    }

    public static PotionView potion(Potion potion) {
        if (potion.isNeutral()) {
            return new PotionView("NEUTRALE", true, false);
        }
        return new PotionView(potion.getColor() + " " + potion.getSign(), false, potion.isNegative());
    }

    public static LaboratoryView laboratory(PrivateLaboratory lab, Map<Set<Ingredient>, Potion> resultsTriangle) {
        return new LaboratoryView(
            ingredientNames(lab.getIngredients()),
            experimentResults(resultsTriangle),
            deductionGrid(lab.getDeductionGrid())
        );
    }

    public static DeductionGridView deductionGrid(DeductionGrid grid) {
        List<Ingredient> ingredients = grid.getIngredients();
        List<AlchemicFormula> alchemics = grid.getAlchemics();
        List<List<Boolean>> excluded = new ArrayList<>();

        for (int ingIdx = 0; ingIdx < ingredients.size(); ingIdx++) {
            List<Boolean> row = new ArrayList<>();
            for (int alcIdx = 0; alcIdx < alchemics.size(); alcIdx++) {
                row.add(grid.isExcluded(ingredients.get(ingIdx), alchemics.get(alcIdx)));
            }
            excluded.add(List.copyOf(row));
        }

        return new DeductionGridView(
            ingredientNames(ingredients),
            alchemics.stream().map(GameViewModels::formatFormula).toList(),
            List.copyOf(excluded)
        );
    }

    public static TargetStatusView targetStatus(StudentState studentState, int gold, int reputation) {
        return new TargetStatusView(
            studentState.name(),
            studentState == StudentState.HAPPY,
            gold,
            reputation
        );
    }

    private static ResourceGainView resourcesFor(String slotId, List<GameConfig.SlotSpec> allSlots) {
        return allSlots.stream()
            .filter(spec -> spec.id().equals(slotId))
            .findFirst()
            .map(spec -> new ResourceGainView(spec.ingredientCount(), spec.favorCount()))
            .orElse(new ResourceGainView(0, 0));
    }

    private static ResourceGainView resourceGain(Resources resources) {
        return new ResourceGainView(resources.ingredientCount(), resources.favorCount());
    }

    private static List<ExperimentResultView> experimentResults(Map<Set<Ingredient>, Potion> resultsTriangle) {
        return resultsTriangle.entrySet().stream()
            .map(entry -> {
                List<String> names = entry.getKey().stream()
                    .map(Ingredient::getName)
                    .sorted()
                    .toList();
                return new ExperimentResultView(names.get(0), names.get(1), potion(entry.getValue()));
            })
            .toList();
    }

    private static String formatFormula(AlchemicFormula formula) {
        StringBuilder builder = new StringBuilder();
        for (Atom atom : formula.getAtoms()) {
            if (builder.length() > 0) {
                builder.append("  ");
            }
            builder.append(atom.getColor().name().charAt(0));
            builder.append(atom.getSign() == Sign.POSITIVE ? "+" : "-");
            builder.append(atom.getSize() == Size.BIG ? "G" : "s");
        }
        return builder.toString();
    }
}
