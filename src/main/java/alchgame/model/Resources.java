package alchgame.model;

/**
 * Resources — descrive la quantità di risorse assegnate da uno Slot
 * dello SpazioOrdine (conteggi; le carte concrete sono pescate dai mazzi del Board).
 */
public record Resources(int ingredientCount, int favorCount) { }
