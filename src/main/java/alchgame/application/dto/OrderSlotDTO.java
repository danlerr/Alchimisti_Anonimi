package alchgame.application.dto;

import java.util.List;

public record OrderSlotDTO(
    String slotId,
    String assignedPlayerName,   // null se libero
    List<String> rewards
) {
    public boolean isTaken() { return assignedPlayerName != null && !assignedPlayerName.isBlank(); }
}
