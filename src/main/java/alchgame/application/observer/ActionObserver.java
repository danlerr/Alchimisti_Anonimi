package alchgame.application.observer;

public interface ActionObserver {
    /** Notifica che un'azione ha mutato lo stato: provoca un refresh della vista. */
    void onActionPerformed();
}
