package alchgame.controller;

import alchgame.model.game.AlchGame;
import alchgame.model.player.Player;

import java.util.List;

/**
 * Controller del caso d'uso "dichiara azioni" (UC02).
 * Espone solo le operazioni di sistema dell'SSD; il player è passato esplicitamente,
 * così il controller non gestisce stato "current player".
 */
public class DeclarationController {

    private final AlchGame alchGame;

    public DeclarationController(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    public List<Player> getDeclarationPhaseOrder() {
        return alchGame.getCurrentRound().declarationPhase().getPhaseOrder(alchGame.getPlayers());
    }

    public List<String> getActionList() {
        return alchGame.getCurrentRound().getBoard().getActionSpaceIds();
    }

    public boolean canPlayerDeclare(Player player) {
        return player.getActionCubes() > 0;
    }

    public void declareAction(Player player, String actionSpaceId) {
        alchGame.getCurrentRound().declarationPhase().declareAction(player, actionSpaceId);
    }
}
