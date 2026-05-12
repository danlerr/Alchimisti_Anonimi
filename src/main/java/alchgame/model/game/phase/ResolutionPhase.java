package alchgame.model.game.phase;

import alchgame.model.board.Board;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ResolutionPhase extends Phase {

    private final List<String> resolutionOrder;
    private int actionIdx = 0;
    private List<Player> currentQueue = List.of();
    private int playerIdx = 0;
    private boolean queueInitialized = false;

    public ResolutionPhase(Board board, int startingPlayerIndex, List<String> resolutionOrder) {
        super(board, startingPlayerIndex);
        this.resolutionOrder = List.copyOf(resolutionOrder);
    }

    @Override
    public List<Player> getPhaseOrder(List<Player> players) {
        return board.getWakeUpOrder();
    }

    public String currentActionId() {
        ensureQueue();
        return resolutionOrder.get(actionIdx);
    }

    public Player currentPlayer() {
        ensureQueue();
        return currentQueue.get(playerIdx);
    }

    public boolean isComplete() {
        ensureQueue();
        return actionIdx >= resolutionOrder.size();
    }

    public void markCurrentPlayerResolved() {
        ensureQueue();
        playerIdx++;
        if (playerIdx >= currentQueue.size()) {
            advanceToNextNonEmptyAction();
        }
    }

    private List<Player> getResolutionOrderFor(String actionSpaceId) {
        List<Player> declared = board.getActionSpace(actionSpaceId).getDeclaredPlayers();
        List<Player> order = new ArrayList<>();
        for (Player player : board.getWakeUpOrder()) {
            if (declared.contains(player)) {
                order.add(player);
            }
        }
        return order;
    }

    private void ensureQueue() {
        if (!queueInitialized) {
            loadQueueForCurrentAction();
            queueInitialized = true;
        }
    }

    private void loadQueueForCurrentAction() {
        if (actionIdx >= resolutionOrder.size()) {
            currentQueue = List.of();
            playerIdx = 0;
            return;
        }
        currentQueue = getResolutionOrderFor(resolutionOrder.get(actionIdx));
        playerIdx = 0;
        if (currentQueue.isEmpty()) {
            advanceToNextNonEmptyAction();
        }
    }

    private void advanceToNextNonEmptyAction() {
        actionIdx++;
        if (actionIdx >= resolutionOrder.size()) {
            currentQueue = List.of();
            playerIdx = 0;
            return;
        }
        loadQueueForCurrentAction();
    }
}
