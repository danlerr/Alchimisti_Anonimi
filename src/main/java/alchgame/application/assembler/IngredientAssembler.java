package alchgame.application.assembler;

import alchgame.application.dto.IngredientDTO;
import alchgame.model.alchemy.Ingredient;

public class IngredientAssembler {

    public static IngredientDTO toDTO(Ingredient ingredient) {
        return new IngredientDTO(ingredient.getId(), ingredient.getName());
    }
}
