package alchgame.view;

import java.util.List;

public record DeductionGridView(List<String> ingredientNames,
                                List<String> alchemicLabels,
                                boolean[][] excluded) {
}
