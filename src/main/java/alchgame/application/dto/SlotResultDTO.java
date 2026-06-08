package alchgame.application.dto;

import java.util.List;

public record SlotResultDTO(List<String> rewards, List<IngredientDTO> receivedIngredients) {}
