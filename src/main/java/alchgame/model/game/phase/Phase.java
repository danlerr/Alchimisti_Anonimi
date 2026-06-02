package alchgame.model.game.phase;

import alchgame.model.player.Player;

import java.util.Optional;

/**
 * La sequenza ORDER → DECLARATION → RESOLUTION è codificata nella catena next(),
 * non in strutture esterne. Le fasi sono istanziate lazily: next() legge il tabellone
 * aggiornato al momento della transizione.
 */
public sealed interface Phase permits OrderPhase, DeclarationPhase, ResolutionPhase {

    boolean isComplete();
    Player getCurrentPlayer();
    void nextPlayer();
    Optional<Phase> next();
    //getOrder();
}
