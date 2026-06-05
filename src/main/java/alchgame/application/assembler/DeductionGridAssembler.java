package alchgame.application.assembler;

import alchgame.application.dto.DeductionGridDTO;
import alchgame.application.dto.IngredientDTO;
import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.alchemy.Atom;
import alchgame.model.alchemy.Color;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.Sign;
import alchgame.model.alchemy.Size;
import alchgame.model.player.DeductionGrid;

import java.util.ArrayList;
import java.util.List;

public class DeductionGridAssembler {

    public static DeductionGridDTO toDTO(DeductionGrid grid) {
        List<Ingredient> ings = grid.getIngredients();
        List<AlchemicFormula> alcs = grid.getAlchemics();

        List<IngredientDTO> ingredients = ings.stream()
                .map(IngredientAssembler::toDTO)
                .toList();

        List<String> alcLabels = new ArrayList<>();
        for (int a = 0; a < alcs.size(); a++)
            alcLabels.add("  [" + (a + 1) + "]  " + formatFormula(alcs.get(a)));

        boolean[][] excluded = new boolean[alcs.size()][ings.size()];
        for (int a = 0; a < alcs.size(); a++)
            for (int i = 0; i < ings.size(); i++)
                excluded[a][i] = grid.isExcluded(ings.get(i), alcs.get(a));

        return new DeductionGridDTO(ingredients, alcLabels, excluded);
    }

    private static String formatFormula(AlchemicFormula formula) {
        StringBuilder sb = new StringBuilder();
        for (Color color : Color.real()) {
            Atom atom = formula.getAtomByColor(color);
            if (atom == null) continue;
            if (!sb.isEmpty()) sb.append(' ');
            sb.append(colorChar(color))
              .append(':')
              .append(sizeChar(atom.getSize()))
              .append(signChar(atom.getSign()));
        }
        return sb.toString();
    }

    private static char colorChar(Color color) {
        return switch (color) {
            case RED   -> 'R';
            case GREEN -> 'G';
            case BLUE  -> 'B';
            default    -> '?';
        };
    }

    private static char sizeChar(Size size) {
        return switch (size) {
            case BIG   -> 'G';
            case SMALL -> 'P';
        };
    }

    private static char signChar(Sign sign) {
        return switch (sign) {
            case POSITIVE -> '+';
            case NEGATIVE -> '-';
            case NEUTRAL  -> '~';
        };
    }
}
