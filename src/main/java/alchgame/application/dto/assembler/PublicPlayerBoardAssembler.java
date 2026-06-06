package alchgame.application.dto.assembler;

import alchgame.application.dto.PublicPlayerBoardDTO;
import alchgame.model.player.Player;

public class PublicPlayerBoardAssembler {

    public static PublicPlayerBoardDTO toDTO(Player player) {
        return new PublicPlayerBoardDTO(
                player.getName(),
                player.getGold(),
                player.getPublicPlayerBoard().getPublishedResults().stream()
                        .map(PotionAssembler::toDTO)
                        .toList()
        );
    }
}
