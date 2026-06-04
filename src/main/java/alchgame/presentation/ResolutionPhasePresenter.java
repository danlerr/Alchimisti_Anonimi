package alchgame.presentation;

import alchgame.model.board.Board;
import alchgame.model.game.Round;
import alchgame.model.game.phase.ResolutionPhase;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ResolutionPhasePresenter {

    private record Step(String actionId, String playerName, Player player) {}

    private final ActionDispatcher dispatcher;
    private final Board board;
    private final Supplier<Round> roundSupplier;
    private final GameView view;

    public ResolutionPhasePresenter(ActionDispatcher dispatcher,
                                    Board board,
                                    Supplier<Round> roundSupplier,
                                    GameView view) {
        this.dispatcher = dispatcher;
        this.board = board;
        this.roundSupplier = roundSupplier;
        this.view = view;
    }

    public void run() {
        Round round = roundSupplier.get();
        view.showPhaseHeader("RISOLUZIONE");

        // Ricostruisce l'ordine di risoluzione dal tabellone,
        // specchio di ResolutionPhase.buildOrder() che non espone l'actionId corrente.
        List<Step> steps = buildResolutionOrder();
        int total = steps.size();

        int cursor = 0;
        while (round.getCurrentPhase() instanceof ResolutionPhase) {
            Step step = steps.get(cursor);
            Player player = step.player();

            view.showResolutionStep(cursor + 1, total, step.actionId(), step.playerName());
            dispatcher.dispatch(step.actionId());
            // dispatcher chiama il UC controller → observer chain avanza il cursore in ResolutionPhase

            view.showPlayerStatus(player.getGold(), player.getReputation(), player.getActionCubes());
            view.showIngredients(player.getIngredientsFromLab().stream()
                    .map(i -> i.getName()).toList());
            cursor++;
        }
    }

    private List<Step> buildResolutionOrder() {
        List<Player> wakeUpOrder = board.getWakeUpOrder();
        List<Step> result = new ArrayList<>();
        for (String actionId : board.getActionSpaceIds()) {
            List<Player> declared = board.getActionSpace(actionId).getDeclaredPlayers();
            for (Player p : wakeUpOrder) {
                long count = declared.stream().filter(p::equals).count();
                for (int i = 0; i < count; i++)
                    result.add(new Step(actionId, p.getName(), p));
            }
        }
        return result;
    }
}
