package alchgame.application.dto;

import java.util.List;

public record DeductionGridDTO(
        List<IngredientDTO> ingredients,
        List<String> alchemicLabels,
        boolean[][] excluded
) {}
