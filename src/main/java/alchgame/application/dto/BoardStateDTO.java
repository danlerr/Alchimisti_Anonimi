package alchgame.application.dto;

import java.util.List;
import java.util.Map;

public record BoardStateDTO(
        List<OrderSlotDTO> orderSlots,
        List<String> wakeUpOrder,
        List<String> actionIds,
        Map<String, List<String>> declarantsByAction
) {}
