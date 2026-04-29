package alchgame.controller;

import java.util.ArrayList;
import java.util.List;

import alchgame.service.GameSetupService;

/**
 * Gestisce il caso d'uso "Inizia Partita":
 * raccoglie numero giocatori e nomi, poi delega il setup della partita.
 */
public class StartGameHandler {

    private final GameSetupService gameSetupService;
    private int expectedPlayers;
    private final List<String> names = new ArrayList<>();

    public StartGameHandler(GameSetupService gameSetupService) {
        this.gameSetupService = gameSetupService;
    }

    public void setPlayerNumber(int n) {
        gameSetupService.validatePlayerNumber(n);
        this.expectedPlayers = n;
        this.names.clear();
    }

    public void setPlayerName(String name) {
        if (expectedPlayers == 0)
            throw new IllegalStateException("Numero giocatori non impostato.");
        gameSetupService.validatePlayerName(name, names, expectedPlayers);
        names.add(name);
    }

    public void startGame() {
        gameSetupService.validatePlayerNamesCount(names, expectedPlayers);
        gameSetupService.initializePlayers(names);
    }

    public int getMinPlayers() { return gameSetupService.getMinPlayers(); }
    public int getMaxPlayers() { return gameSetupService.getMaxPlayers(); }
}
