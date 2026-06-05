package alchgame.presentation;

import alchgame.application.GameController;
import alchgame.application.dto.PlayerDTO;
import alchgame.application.observer.GameStateDTO;

public class ResolutionPhasePresenter {

    private final ActionDispatcher dispatcher;
    private final GameController gameController;
    private final GameView view;

    public ResolutionPhasePresenter(ActionDispatcher dispatcher,
                                    GameController gameController,
                                    GameView view) {
        this.dispatcher = dispatcher;
        this.gameController = gameController;
        this.view = view;
    }

    public void showPhaseStart() {
        view.showPhaseHeader("RISOLUZIONE");
    }

    /**
     * Reattivo e senza loop. Sull'evento di turno (fresco) risolve l'azione via
     * dispatcher; l'azione emette un TURN_REFRESHED che mostra lo stato fresco
     * (il risultato: oro/ingredienti aggiornati) e fa avanzare allo step successivo.
     */
    public void handleTurn(GameStateDTO state) {
        PlayerDTO player = state.currentPlayer();

        if (state.type() == GameStateDTO.EventType.TURN_REFRESHED) {
            view.showPlayerStatus(player.gold(), player.reputation(), player.actionCubes());
            view.showIngredients(player.ingredients().stream()
                    .map(i -> i.name()).toList());
            gameController.endTurn();
            return;
        }

        view.showResolutionStep(state.currentActionId(), player.name());
        dispatcher.dispatch(state.currentActionId());   // azione → onActionPerformed → TURN_REFRESHED
    }
}
