package alchgame.application.dto;

import java.util.List;

public record OrderSlotDTO(
    String slotId,
    String assignedPlayerName,   // null se libero
    List<String> rewards,
    boolean taken
) {}
