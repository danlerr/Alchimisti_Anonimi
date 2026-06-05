package alchgame.application.observer;

import alchgame.model.player.Player;

import java.util.List;

public record GameStateDTO(
    EventType type,
    PhaseType phaseType,
    Player currentPlayer,
    String currentActionId,
    int roundNumber,
    List<Player> finalRanking
) {
    public enum EventType { TURN_ADVANCED, PHASE_CHANGED, ROUND_ENDED, GAME_OVER }
    public enum PhaseType { ORDER, DECLARATION, RESOLUTION }
}
