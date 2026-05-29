package alchgame.controller;

import alchgame.model.board.Resources;
import alchgame.model.game.AlchGame;
import alchgame.model.player.Player;

import java.util.List;

/**
 * Controller del caso d'uso "scegli la posizione nel tracciato di risveglio" (UC01).
 * Espone solo le operazioni di sistema dell'SSD; il player è passato esplicitamente
 * (realizzazione dell'attore implicito dell'SSD), così il controller non gestisce
 * stato "current player".
 */
public class OrderController {

    private final AlchGame alchGame;

    public OrderController(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    public List<Player> getOrderPhaseOrder() {
        return alchGame.getCurrentRound().orderPhase().getPhaseOrder(alchGame.getPlayers());
    }

    public List<String> getAvailableSlots() {
        return alchGame.getCurrentRound().orderPhase().getAvailableSlotIds();
    }

    public Resources chooseSlot(Player player, String orderSlotID) {
        return alchGame.getCurrentRound().orderPhase().chooseSlot(player, orderSlotID);
    }
}
