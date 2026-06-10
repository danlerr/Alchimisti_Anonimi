package alchgame.application;

import java.util.ArrayList;
import java.util.List;

import alchgame.model.game.AlchGame;

/**
 * Gestisce il caso d'uso "Inizia Partita":
 * raccoglie numero giocatori e nomi, poi delega il setup della partita.
 */
public class StartGameController{

    private final AlchGame alchGame;
    private int expectedPlayers;
    private final List<String> names = new ArrayList<>();


    public StartGameController(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    public void setPlayerNumber(int n) {
        alchGame.validatePlayerNumber(n);
        this.expectedPlayers = n;
        this.names.clear();
    }

    public void setPlayerName(String name) {
        alchGame.validatePlayerName(name, names);
        names.add(name);
    }

    public void startGame() {
        alchGame.startGame(names);
    }

    public boolean needsMorePlayerNames() {
    return names.size() < expectedPlayers;
    }

    public int getInsertedPlayersCount() {
        return names.size();
    }

    public int getMinPlayers() {
        return alchGame.getMinPlayers();
    }

    public int getMaxPlayers() {
        return alchGame.getMaxPlayers();
    }

}
