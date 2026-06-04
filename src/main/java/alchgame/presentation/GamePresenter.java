package alchgame.presentation;

import alchgame.application.GameController;
import alchgame.application.observer.GameEvent;
import alchgame.application.observer.GameObserver;
import alchgame.model.game.AlchGame;
import alchgame.model.game.Round;
import alchgame.model.game.phase.Phase;
import alchgame.model.game.phase.OrderPhase;
import alchgame.model.game.phase.DeclarationPhase;
import alchgame.model.game.phase.ResolutionPhase;
import alchgame.model.player.Player;
import alchgame.resources.GameConfig;

import java.util.List;

/**
 * Orchestratore della presentazione. Implementa GameObserver per ricevere
 * gli eventi dal GameController e guidare la UI di conseguenza.
 * Il loop principale è driven dal model: dopo ogni fase il presenter
 * legge getCurrentPhase() per sapere cosa fare dopo.
 */
public class GamePresenter implements GameObserver {

    private final AlchGame alchGame;
    private final GameController gameController;
    private final GameView view;
    private final SetupPresenter setupPresenter;
    private final OrderPhasePresenter orderPhasePresenter;
    private final DeclarationPhasePresenter declarationPhasePresenter;
    private final ResolutionPhasePresenter resolutionPhasePresenter;

    private int roundNumber = 1;

    public GamePresenter(AlchGame alchGame,
                         GameController gameController,
                         GameView view,
                         SetupPresenter setupPresenter,
                         OrderPhasePresenter orderPhasePresenter,
                         DeclarationPhasePresenter declarationPhasePresenter,
                         ResolutionPhasePresenter resolutionPhasePresenter) {
        this.alchGame = alchGame;
        this.gameController = gameController;
        this.view = view;
        this.setupPresenter = setupPresenter;
        this.orderPhasePresenter = orderPhasePresenter;
        this.declarationPhasePresenter = declarationPhasePresenter;
        this.resolutionPhasePresenter = resolutionPhasePresenter;
    }

    public void run() {
        gameController.attach(this);
        setupPresenter.run();

        // Loop principale: ogni iterazione esterna = un round
        while (!alchGame.isOver()) {
            Round round = alchGame.getCurrentRound();
            view.showRoundStart(roundNumber, GameConfig.TOTAL_ROUNDS);

            // Loop interno: ogni iterazione = una fase del round corrente
            // Esce quando currentPhase diventa null (round terminato)
            Phase phase;
            while ((phase = round.getCurrentPhase()) != null) {
                if (phase instanceof OrderPhase) {
                    orderPhasePresenter.run();
                } else if (phase instanceof DeclarationPhase) {
                    declarationPhasePresenter.run();
                } else if (phase instanceof ResolutionPhase) {
                    resolutionPhasePresenter.run();
                }
            }
            // Il round è finito. Se la partita continua, roundNumber viene
            // incrementato su ROUND_ENDED. Se è l'ultimo round, isOver() sarà true.
        }

        // Partita terminata: mostra classifica finale
        List<Player> ranked = alchGame.calculateFinalScores();
        view.showGameOver(ranked.stream()
                .map(p -> new PlayerResult(p.getName(), p.getReputation(), p.getGold()))
                .toList());
    }

    @Override
    public void onGameEvent(GameEvent event) {
        switch (event) {
            case ROUND_ENDED -> {
                view.showRoundEnd(roundNumber);
                roundNumber++;
            }
            case TURN_ADVANCED, PHASE_CHANGED, GAME_OVER -> {
                // Il flusso è gestito dal loop in run() leggendo il model.
                // Nessuna azione UI diretta necessaria qui.
            }
        }
    }
}
