package alchgame.application.dto;

import java.util.List;
import java.util.Map;

public record BoardStateDTO(
        Map<String, String> orderSlots,
        List<String> wakeUpOrder,
        List<String> actionIds,
        Map<String, List<String>> declarantsByAction,
        Map<String, List<String>> slotRewardDescriptions
) {}
