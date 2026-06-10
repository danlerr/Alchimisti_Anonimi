package alchgame.model.game;

import alchgame.model.game.phase.Phase;
import alchgame.model.player.Player;

import java.util.List;

public sealed interface GameTransition permits
        GameTransition.TurnAdvanced,
        GameTransition.PhaseChanged,
        GameTransition.RoundEnded,
        GameTransition.GameOver {

    record TurnAdvanced(Phase phase, int roundNumber) implements GameTransition {}
    record PhaseChanged(Phase phase, int roundNumber) implements GameTransition {}
    record RoundEnded(int roundNumber) implements GameTransition {}
    record GameOver(List<Player> ranking, int roundNumber) implements GameTransition {}
}
