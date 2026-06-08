package alchgame.application.dto.assembler;

import alchgame.application.dto.SlotResultDTO;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.board.slotReward.SlotReward;

import java.util.List;

public class SlotResultAssembler {

    public static SlotResultDTO toDTO(List<SlotReward> rewards, List<Ingredient> received) {
    return new SlotResultDTO(
        rewards.stream().map(SlotReward::describe).toList(),
        received.stream().map(IngredientAssembler::toDTO).toList()
    );
}
}
