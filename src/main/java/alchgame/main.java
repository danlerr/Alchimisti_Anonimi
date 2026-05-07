package alchgame;

/**
 * Entry point dell'applicazione.
 *
 * Delega l'avvio al bootstrapper, mantenendo il metodo main privo di logica di
 * configurazione o dominio.
 */
public class Main {
    public static void main(String[] args) {
        GameBootstrapper.run();
    }
}
