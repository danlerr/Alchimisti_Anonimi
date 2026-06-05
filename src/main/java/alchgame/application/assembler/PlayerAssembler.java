package alchgame.application.assembler;

import alchgame.application.dto.PlayerDTO;
import alchgame.model.player.Player;

public class PlayerAssembler {

    public static PlayerDTO toDTO(Player player) {
        return new PlayerDTO(
                player.getName(),
                player.getGold(),
                player.getReputation(),
                player.getActionCubes(),
                player.getIngredientsFromLab().stream()
                        .map(IngredientAssembler::toDTO)
                        .toList(),
                player.getFavorCards().stream()
                        .map(f -> f.getName())
                        .toList()
        );
    }
}
