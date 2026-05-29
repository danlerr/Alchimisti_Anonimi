package alchgame.service;

import alchgame.model.player.Player;

import java.util.List;

/**
 * Observer del flusso di gioco: GameSession notifica gli eventi del loop,
 * il presentation layer reagisce senza conoscere oggetti di dominio delle fasi.
 */
public interface GameFlowHandler {
    void onRoundStart(int current, int total);
    void onOrderPhase();
    void onDeclarationPhase();
    void onResolutionPhase();
    void onRoundEnd(int current);
    void onGameOver(List<Player> players);
}
