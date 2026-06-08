package alchgame.application.dto.assembler;

import alchgame.application.dto.PlayerDTO;
import alchgame.model.player.Player;

public class PlayerAssembler {

    public static PlayerDTO toDTO(Player player) {
        return new PlayerDTO(
                player.getName(),
                player.getGold(),
                player.getReputation(),
                player.getActionCubes(),
                IngredientAssembler.toDTOList(player.getIngredientsFromLab()),
                player.getFavorCards().stream()
                        .map(f -> f.getName())
                        .toList(),
                DeductionGridAssembler.toDTO(player.getDeductionGrid()),
                ResultsTriangleAssembler.toDTOs(player.getResultsTriangle())
        );
    }
}
