package alchgame.application.dto;

import java.util.List;

public record PlayerDTO(
        String name,
        int gold,
        int reputation,
        int actionCubes,
        List<IngredientDTO> ingredients,
        List<String> favors,
        DeductionGridDTO deductionGrid,
        List<ExperimentResultDTO> experimentResults
) {}
