package alchgame.application.dto;

import java.util.List;

public record GameStateDTO(
    EventType type,
    PhaseType phaseType,
    PlayerDTO currentPlayer,
    String currentActionId,
    int roundNumber,
    List<PlayerDTO> finalRanking,
    BoardStateDTO boardState
) {
    public enum EventType { TURN_ADVANCED, PHASE_CHANGED, ROUND_ENDED, GAME_OVER }
    public enum PhaseType { ORDER, DECLARATION, RESOLUTION }
}
