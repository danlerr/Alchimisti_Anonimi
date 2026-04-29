package alchgame.model.game;

import alchgame.model.player.Player;

final class CleanupPhase implements TurnPhase {

    @Override
    public TurnPhaseType type() {
        return TurnPhaseType.CLEANUP;
    }

    @Override
    public TurnPhase next() {
        return TurnPhases.order();
    }

    @Override
    public void endRound(GameSession game) {
        for (Player player : game.getPlayers()) {
            player.restoreActionCubes(game.getStartingActionCubes());
            int favors = player.consumePendingFavors();
            game.getBoard().dealFavors(player, favors);
        }

        game.getBoard().resetRound();
        if (game.getCurrentRound() >= game.getTotalRounds()) {
            game.finish();
            return;
        }

        int nextRound = game.getCurrentRound() + 1;
        int nextStartingPlayerIndex = (game.getStartingPlayerIndex() + 1) % game.getPlayers().size();
        game.startNextRound(nextRound, nextStartingPlayerIndex);
    }
}
