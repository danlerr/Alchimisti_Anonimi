package alchgame.application.dto.assembler;

import alchgame.application.dto.SlotResultDTO;
import alchgame.model.board.slotReward.SlotReward;

import java.util.List;

public class SlotResultAssembler {

    public static SlotResultDTO toDTO(List<SlotReward> rewards) {
        return new SlotResultDTO(
            rewards.stream().map(SlotReward::describe).toList()
        );
    }
}
