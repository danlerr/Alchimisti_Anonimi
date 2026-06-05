package alchgame.application;

import java.util.ArrayList;
import java.util.List;

import alchgame.service.StartGameService;

/**
 * Gestisce il caso d'uso "Inizia Partita":
 * raccoglie numero giocatori e nomi, poi delega il setup della partita.
 */
public class StartGameController{

    private final StartGameService startGameService;
    private int expectedPlayers;
    private final List<String> names = new ArrayList<>();


    public StartGameController(StartGameService startGameService) {
        this.startGameService = startGameService;
    }

    public void setPlayerNumber(int n) {
        startGameService.validatePlayerNumber(n);
        this.expectedPlayers = n;
        this.names.clear();
    }

    public void setPlayerName(String name) {
        startGameService.validatePlayerName(name, names);
        names.add(name);
    }

    public void startGame() {
        startGameService.startGame(names);
    }

    public boolean needsMorePlayerNames() {        //il loop di richiesta nomi si ferma in base al controller e non in base alla view
    return names.size() < expectedPlayers;
    }

    public int getInsertedPlayersCount() {
        return names.size();
    }

    public int getMinPlayers() {
        return startGameService.getMinPlayers();
    }

    public int getMaxPlayers() {
        return startGameService.getMaxPlayers();
    }

}
