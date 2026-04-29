package alchgame.service;

import alchgame.model.game.GameLifecycle;
import alchgame.model.game.GamePhase;
import alchgame.model.game.GameSession;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestisce il flusso della partita: fasi del round, ordine dei giocatori e avanzamento round.
 */
public class GameFlowService {

    private final GameSession game;

    public GameFlowService(GameSession alchGame) {
        this.game = alchGame;
    }

    /** Ordine fase ordine: senso orario dal primo giocatore; paralizzati in coda consumando il flag. */
    public List<Player> getOrderPhaseOrder() {
        requirePlaying();
        game.setCurrentPhase(GamePhase.ORDER);

        List<Player> normal = new ArrayList<>();
        List<Player> paralyzed = new ArrayList<>();
        List<Player> players = game.getPlayers();
        int n = players.size();
        for (int i = 0; i < n; i++) {
            Player player = players.get((game.getStartingPlayerIndex() + i) % n);
            if (player.isParalyzed()) {
                paralyzed.add(player);
                player.clearParalysis();
            } else {
                normal.add(player);
            }
        }
        normal.addAll(paralyzed);
        return normal;
    }

    /** Ordine dichiarazione: inverso wake-up (bottom -> top). */
    public List<Player> getDeclarationPhaseOrder() {
        requirePlaying();
        game.setCurrentPhase(GamePhase.DECLARATION);

        List<Player> wakeUp = new ArrayList<>(game.getBoard().getWakeUpOrder());
        Collections.reverse(wakeUp);
        return wakeUp;
    }

    /**
     * Ordine risoluzione per uno spazio azione: wake-up (top -> bottom),
     * filtrato ai soli player che hanno dichiarato quell'azione.
     */
    public List<Player> getResolutionOrderFor(String actionSpaceId) {
        requirePlaying();
        game.setCurrentPhase(GamePhase.RESOLUTION);

        List<Player> declared = game.getBoard().getActionSpace(actionSpaceId).getDeclaredPlayers();
        List<Player> resolutionOrder = new ArrayList<>();
        for (Player player : game.getBoard().getWakeUpOrder()) {
            for (Player declaredPlayer : declared) {
                if (declaredPlayer.equals(player))
                    resolutionOrder.add(player);
            }
        }
        return resolutionOrder;
    }

    public void endRound() {
        requirePlaying();
        game.setCurrentPhase(GamePhase.CLEANUP);

        for (Player player : game.getPlayers()) {
            player.restoreActionCubes(game.getStartingActionCubes());
            int favors = player.consumePendingFavors();
            game.getBoard().dealFavors(player, favors);
        }

        game.getBoard().resetRound();
        if (game.getCurrentRound() >= game.getTotalRounds()) {
            game.setLifecycle(GameLifecycle.ENDED);
            game.setCurrentPhase(null);
            return;
        }

        int nextRound = game.getCurrentRound() + 1;
        int nextStartingPlayerIndex = (game.getStartingPlayerIndex() + 1) % game.getPlayers().size();
        game.setCurrentRound(nextRound);
        game.setStartingPlayerIndex(nextStartingPlayerIndex);
        game.setCurrentPlayerIndex(nextStartingPlayerIndex);
        game.setCurrentPhase(GamePhase.ORDER);
    }

    public void endGame() {
        requirePlaying();
        game.end();
    }

    public int getCurrentRound() { return game.getCurrentRound(); }
    public int getTotalRounds() { return game.getTotalRounds(); }
    public GamePhase getCurrentPhase() { return game.getCurrentPhase(); }

    private void requirePlaying() {
        if (game.getLifecycle() != GameLifecycle.PLAYING)
            throw new IllegalStateException("Operazione ammessa solo durante PLAYING.");
    }
}
