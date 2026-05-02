package alchgame.view.viewmodel;

import java.util.List;

public record LaboratoryView(List<String> ingredientNames,
                             List<ExperimentResultView> experimentResults,
                             DeductionGridView deductionGrid) {
}
