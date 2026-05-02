package alchgame.view.viewmodel;

import java.util.List;

public record DeductionGridView(List<String> ingredientNames,
                                List<String> alchemicLabels,
                                List<List<Boolean>> excluded) {
}
