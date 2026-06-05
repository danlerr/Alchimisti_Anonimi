package alchgame.config;

/**
 * Dato di configurazione che descrive un atomo alchemico prima che venga
 * trasformato in un oggetto del dominio.
 */
public record AtomSpec(String color, String size, String sign) { }
