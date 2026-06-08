package alchgame.application.dto.assembler;

import alchgame.application.dto.IngredientDTO;
import alchgame.model.alchemy.Ingredient;

import java.util.List;

public class IngredientAssembler {

    public static IngredientDTO toDTO(Ingredient ingredient) {
        return new IngredientDTO(ingredient.getId(), ingredient.getName());
    }

    public static List<IngredientDTO> toDTOList(List<Ingredient> ingredients) {
        return ingredients.stream().map(IngredientAssembler::toDTO).toList();
    }
}
