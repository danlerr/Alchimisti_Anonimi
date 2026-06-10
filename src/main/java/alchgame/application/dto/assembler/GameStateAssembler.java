package alchgame.application.dto.assembler;

import alchgame.application.dto.GameStateDTO;
import alchgame.application.dto.PublicPlayerBoardDTO;
import alchgame.model.board.Board;
import alchgame.model.game.GameTransition;
import alchgame.model.game.phase.DeclarationPhase;
import alchgame.model.game.phase.OrderPhase;
import alchgame.model.game.phase.Phase;
import alchgame.model.game.phase.ResolutionPhase;
import alchgame.model.player.Player;

import java.util.List;

public class GameStateAssembler {

    public static GameStateDTO assemble(GameTransition t, Board board, List<Player> players) {
        return switch (t) {
            case GameTransition.TurnAdvanced(var phase, var round) ->
                turnAdvanced(phase, round, board, players);
            case GameTransition.PhaseChanged(var phase, var round) ->
                phaseChanged(phase, round, board, players);
            case GameTransition.RoundEnded(var round) ->
                roundEnded(round);
            case GameTransition.GameOver(var ranking, var round) ->
                gameOver(ranking, round);
        };
    }

    public static GameStateDTO turnAdvanced(Phase phase, int roundNumber, Board board, List<Player> players) {
        return new GameStateDTO(
                GameStateDTO.EventType.TURN_ADVANCED,
                phaseTypeOf(phase),
                PlayerAssembler.toDTO(phase.getCurrentPlayer()),
                actionIdOf(phase),
                roundNumber,
                null,
                BoardStateAssembler.toDTO(board),
                publicBoardsOf(players)
        );
    }

    public static GameStateDTO phaseChanged(Phase phase, int roundNumber, Board board, List<Player> players) {
        return new GameStateDTO(
                GameStateDTO.EventType.PHASE_CHANGED,
                phaseTypeOf(phase),
                PlayerAssembler.toDTO(phase.getCurrentPlayer()),
                actionIdOf(phase),
                roundNumber,
                null,
                BoardStateAssembler.toDTO(board),
                publicBoardsOf(players)
        );
    }

    public static GameStateDTO roundEnded(int roundNumber) {
        return new GameStateDTO(
                GameStateDTO.EventType.ROUND_ENDED,
                null, null, null,
                roundNumber,
                null,
                null,
                List.of()
        );
    }

    public static GameStateDTO gameOver(List<Player> ranking, int roundNumber) {
        return new GameStateDTO(
                GameStateDTO.EventType.GAME_OVER,
                null, null, null,
                roundNumber,
                ranking.stream().map(PlayerAssembler::toDTO).toList(),
                null,
                List.of()
        );
    }

    private static List<PublicPlayerBoardDTO> publicBoardsOf(List<Player> players) {
        return players.stream().map(PublicPlayerBoardAssembler::toDTO).toList();
    }

    private static GameStateDTO.PhaseType phaseTypeOf(Phase phase) {
        return switch (phase) {
            case OrderPhase _       -> GameStateDTO.PhaseType.ORDER;
            case DeclarationPhase _ -> GameStateDTO.PhaseType.DECLARATION;
            case ResolutionPhase _  -> GameStateDTO.PhaseType.RESOLUTION;
        };
    }

    private static String actionIdOf(Phase phase) {
        return switch (phase) {
            case ResolutionPhase rp -> rp.getCurrentActionId();
            default -> null;
        };
    }
}
