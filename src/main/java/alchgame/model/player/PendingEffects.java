package alchgame.model.player;

class PendingEffects {

    private int cubeModifier  = 0;
    private int pendingFavors = 0;
    private boolean paralyzed = false;

    void scheduleCubeModifier(int delta) { cubeModifier += delta; }
    void scheduleFavor(int n)            { pendingFavors += n; }
    void scheduleParalysis()             { paralyzed = true; }
    boolean isParalyzed()                { return paralyzed; }
    void clearParalysis()                { paralyzed = false; }

    int consumePendingFavors() {
        int n = pendingFavors;
        pendingFavors = 0;
        return n;
    }

    int consumeCubeModifier() {
        int n = cubeModifier;
        cubeModifier = 0;
        return n;
    }
}
