package alchgame.model.player;


import java.util.ArrayList;
import java.util.List;

class PendingEffects {

    private final List<DelayedEffect> queue = new ArrayList<>();
    private boolean paralyzed = false;

    void schedule(DelayedEffect effect) {
        queue.add(effect);
    }

    List<DelayedEffect> drain() {
        List<DelayedEffect> copy = List.copyOf(queue);
        queue.clear();
        return copy;
    }

    void scheduleParalysis() {
        paralyzed = true;
    }

    boolean isParalyzed() {
        return paralyzed;
    }

    void clearParalysis() {
        paralyzed = false;
    }
}
