package alchgame.application.dto.assembler;

import alchgame.application.dto.SlotResultDTO;
import alchgame.model.board.slotReward.SlotRewardStrategy;

import java.util.List;

public class SlotResultAssembler {

    public static SlotResultDTO toDTO(List<SlotRewardStrategy> rewards) {
        return new SlotResultDTO(
            rewards.stream().map(SlotRewardStrategy::describe).toList()
        );
    }
}
