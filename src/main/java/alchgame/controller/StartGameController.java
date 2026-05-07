package alchgame.controller;

import java.util.ArrayList;
import java.util.List;

import alchgame.service.StartGameService;

/**
 * Gestisce il caso d'uso "Inizia Partita":
 * raccoglie numero giocatori e nomi, poi delega il setup della partita.
 */
public class StartGameController {

    private final StartGameService startGameService;
    private final List<String> names = new ArrayList<>();

    public StartGameController(StartGameService startGameService) {
        this.startGameService = startGameService;
    }

    public void setPlayerNumber(int n) {
        startGameService.validatePlayerNumber(n);

    }

    public void setPlayerName(String name) {
        startGameService.validatePlayerName(name, names);
        names.add(name);
    }

    public void startGame() {
        startGameService.startGame(names);
    }

    public int getMinPlayers() { 
        return startGameService.getMinPlayers();
    }

    public int getMaxPlayers() { 
        return startGameService.getMaxPlayers(); 
    }
}
