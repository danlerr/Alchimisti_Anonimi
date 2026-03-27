package alchgame.model;

public interface Target {

    /**
     * Applica l'effetto di una pozione:
     *   - Student + negativa → state = UNHAPPY
     *   - Player  + negativa → malus alla reputation
     */
    void applyEffect(Potion potion);

    /** True se condurre l'esperimento su questo target richiede 1 moneta d'oro. */
    boolean requiresPayment();
}
