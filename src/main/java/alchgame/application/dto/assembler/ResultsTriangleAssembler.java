package alchgame.application.dto.assembler;

import alchgame.application.dto.ExperimentResultDTO;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.Potion;
import alchgame.model.player.ResultsTriangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultsTriangleAssembler {

    public static List<ExperimentResultDTO> toDTOs(ResultsTriangle triangle) {
        Map<Set<Ingredient>, Potion> all = triangle.getAllResults();
        List<ExperimentResultDTO> list = new ArrayList<>();
        for (Map.Entry<Set<Ingredient>, Potion> entry : all.entrySet()) {
            List<Ingredient> pair = new ArrayList<>(entry.getKey());
            list.add(new ExperimentResultDTO(
                    IngredientAssembler.toDTO(pair.get(0)),
                    IngredientAssembler.toDTO(pair.get(1)),
                    PotionAssembler.toDTO(entry.getValue())
            ));
        }
        return List.copyOf(list);
    }
}
