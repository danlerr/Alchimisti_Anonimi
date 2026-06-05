package alchgame.application.assembler;

import alchgame.application.observer.GameStateDTO;
import alchgame.model.board.Board;
import alchgame.model.game.phase.DeclarationPhase;
import alchgame.model.game.phase.OrderPhase;
import alchgame.model.game.phase.Phase;
import alchgame.model.game.phase.ResolutionPhase;
import alchgame.model.player.Player;

import java.util.List;

public class GameStateAssembler {

    public static GameStateDTO phaseChanged(Phase phase, int roundNumber, Board board) {
        return new GameStateDTO(
                GameStateDTO.EventType.PHASE_CHANGED,
                phaseTypeOf(phase),
                PlayerAssembler.toDTO(phase.getCurrentPlayer()),
                actionIdOf(phase),
                roundNumber,
                null,
                BoardStateAssembler.toDTO(board)
        );
    }

    public static GameStateDTO turnAdvanced(Phase phase, int roundNumber, Board board) {
        return new GameStateDTO(
                GameStateDTO.EventType.TURN_ADVANCED,
                phaseTypeOf(phase),
                PlayerAssembler.toDTO(phase.getCurrentPlayer()),
                actionIdOf(phase),
                roundNumber,
                null,
                BoardStateAssembler.toDTO(board)
        );
    }

    public static GameStateDTO roundEnded(int roundNumber) {
        return new GameStateDTO(
                GameStateDTO.EventType.ROUND_ENDED,
                null, null, null,
                roundNumber,
                null,
                null
        );
    }

    public static GameStateDTO gameOver(List<Player> ranking, int roundNumber) {
        return new GameStateDTO(
                GameStateDTO.EventType.GAME_OVER,
                null, null, null,
                roundNumber,
                ranking.stream().map(PlayerAssembler::toDTO).toList(),
                null
        );
    }

    private static GameStateDTO.PhaseType phaseTypeOf(Phase phase) {
        if (phase instanceof OrderPhase)       return GameStateDTO.PhaseType.ORDER;
        if (phase instanceof DeclarationPhase) return GameStateDTO.PhaseType.DECLARATION;
        if (phase instanceof ResolutionPhase)  return GameStateDTO.PhaseType.RESOLUTION;
        return null;
    }

    private static String actionIdOf(Phase phase) {
        if (phase instanceof ResolutionPhase rp) return rp.getCurrentActionId();
        return null;
    }
}
