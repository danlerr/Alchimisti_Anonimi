package alchgame.model.game;

public enum TurnPhase {
    ORDER, DECLARATION, RESOLUTION;

    public TurnPhase next() {
        TurnPhase[] phases = values();
        if (ordinal() + 1 >= phases.length)
            throw new IllegalStateException("RESOLUTION è l'ultima fase del turno.");
        return phases[ordinal() + 1];
    }
}
