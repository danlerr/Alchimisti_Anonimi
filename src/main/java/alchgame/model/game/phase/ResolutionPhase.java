package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ResolutionPhase extends Phase {

    private record ResolutionStep(String actionId, Player player) {}

    private final List<String> resolutionOrder;
    private List<ResolutionStep> steps = null;
    private int cursor = 0;

    public ResolutionPhase(Board board, int startingPlayerIndex, List<String> resolutionOrder) {
        super(board, startingPlayerIndex);
        this.resolutionOrder = List.copyOf(resolutionOrder);
    }

    @Override
    public List<Player> getPhaseOrder(List<Player> players) {
        return board.getWakeUpOrder();
    }

    public String currentActionId() {
        return getSteps().get(cursor).actionId();
    }

    public Player currentPlayer() {
        return getSteps().get(cursor).player();
    }

    public boolean isComplete() {
        return cursor >= getSteps().size();
    }

    public int currentStepIndex() {
        return cursor;
    }

    public int totalSteps() {
        return getSteps().size();
    }

    public void markCurrentPlayerResolved() {
        cursor++;
    }

    private List<ResolutionStep> getSteps() {
        if (steps == null) steps = buildSteps();
        return steps;
    }

    private List<ResolutionStep> buildSteps() {
        List<Player> wakeUpOrder = board.getWakeUpOrder();
        List<ResolutionStep> result = new ArrayList<>();
        for (String actionId : resolutionOrder) {
            List<Player> declared = board.getActionSpace(actionId).getDeclaredPlayers();
            for (Player player : wakeUpOrder) {
                if (declared.contains(player)) {
                    result.add(new ResolutionStep(actionId, player));
                }
            }
        }
        return List.copyOf(result);
    }
}
