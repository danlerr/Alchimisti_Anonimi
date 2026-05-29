package alchgame.model.game;

import alchgame.model.alchemy.Potion;

public interface Target {

    /**
     * Applica l'effetto di una pozione:
     *   - Student + negativa → state = UNHAPPY
     *   - Player  + negativa → malus alla reputation
     */
    void applyEffect(Potion potion);

    /** True se condurre l'esperimento su questo target richiede 1 moneta d'oro. */
    boolean requiresPayment();

    int getPaymentAmount();

    /**
     * Resetta lo stato del target a inizio nuovo round.
     *   - Student: torna HAPPY (il costo del test vale solo nel round corrente)
     *   - Player:  no-op
     */
    void reset();

}
