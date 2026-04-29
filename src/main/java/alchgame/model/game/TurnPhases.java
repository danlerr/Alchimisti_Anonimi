package alchgame.model.game;

//factory/registry interna
//Dato che le fasi non hanno stato interno proprio, 
// possono essere riusate come singleton. In pratica TurnPhases è 
// il posto centralizzato da cui GameSession prende gli oggetti fase.
final class TurnPhases {

    private static final TurnPhase ORDER = new OrderPhase();
    private static final TurnPhase DECLARATION = new DeclarationPhase();
    private static final TurnPhase RESOLUTION = new ResolutionPhase();
    private static final TurnPhase CLEANUP = new CleanupPhase();

    private TurnPhases() {}

    static TurnPhase order() { return ORDER; }
    static TurnPhase declaration() { return DECLARATION; }
    static TurnPhase resolution() { return RESOLUTION; }
    static TurnPhase cleanup() { return CLEANUP; }
}
