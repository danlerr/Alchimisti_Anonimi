package alchgame.controller;

import java.util.ArrayList;
import java.util.List;

import alchgame.service.AlchGame;

/**
 * Gestisce il caso d'uso "Inizia Partita":
 * raccoglie numero giocatori e nomi, poi delega ad AlchGame la costruzione dei Player.
 */
public class StartGameHandler {

    private final AlchGame alchGame;

    private int expectedPlayers;
    private final List<String> names = new ArrayList<>();

    public StartGameHandler(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    public void setPlayerNumber(int n) {
        if (alchGame.isStarted())
            throw new IllegalStateException("Partita già avviata.");
        if (n < AlchGame.MIN_PLAYERS || n > AlchGame.MAX_PLAYERS)
            throw new IllegalArgumentException(
                "Numero giocatori non valido: " + n +
                " (consentito " + AlchGame.MIN_PLAYERS + "–" + AlchGame.MAX_PLAYERS + ").");
        this.expectedPlayers = n;
        this.names.clear();
    }

    public void setPlayerName(String name) {
        if (alchGame.isStarted())
            throw new IllegalStateException("Partita già avviata.");
        if (expectedPlayers == 0)
            throw new IllegalStateException("Numero giocatori non impostato.");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome giocatore vuoto.");
        if (names.contains(name))
            throw new IllegalArgumentException("Nome già usato: " + name);
        if (names.size() >= expectedPlayers)
            throw new IllegalStateException("Tutti i nomi sono già stati inseriti.");
        names.add(name);
    }

    public void startGame() {
        if (alchGame.isStarted())
            throw new IllegalStateException("Partita già avviata.");
        if (names.size() != expectedPlayers)
            throw new IllegalStateException(
                "Nomi mancanti: attesi " + expectedPlayers + ", ricevuti " + names.size() + ".");
        alchGame.initializePlayers(names);
        // TODO prossima iterazione: distribuzione iniziale favor, posizionamento sulla wake-up track.
    }
}
