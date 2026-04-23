package alchgame.controller;

import java.util.ArrayList;
import java.util.List;

import alchgame.GameConfig;
import alchgame.model.DeductionGrid;
import alchgame.model.Player;
import alchgame.model.PrivateLaboratory;
import alchgame.model.PublicPlayerBoard;
import alchgame.model.ResultsTriangle;
import alchgame.service.AlchGame;

/**
 * Gestisce il caso d'uso "Inizia Partita":
 * raccoglie numero giocatori e nomi, costruisce i Player e li registra in AlchGame.
 */
public class StartGameHandler {

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;

    private final AlchGame alchGame;

    private int expectedPlayers;
    private final List<String> names = new ArrayList<>();

    public StartGameHandler(AlchGame alchGame) {
        this.alchGame = alchGame;
    }

    public void setPlayerNumber(int n) {
        if (alchGame.isStarted())
            throw new IllegalStateException("Partita già avviata.");
        if (n < MIN_PLAYERS || n > MAX_PLAYERS)
            throw new IllegalArgumentException(
                "Numero giocatori non valido: " + n + " (consentito " + MIN_PLAYERS + "–" + MAX_PLAYERS + ").");
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

        List<Player> players = new ArrayList<>();
        for (String n : names) {
            players.add(new Player(
                n,
                GameConfig.STARTING_GOLD,
                GameConfig.STARTING_REPUTATION,
                newPrivateLab(),
                new PublicPlayerBoard()));
        }
        alchGame.initializePlayers(players);
        // TODO prossima iterazione: distribuzione iniziale favor, posizionamento sulla wake-up track.
    }

    private PrivateLaboratory newPrivateLab() {
        DeductionGrid grid = new DeductionGrid(alchGame.getIngredients(), alchGame.getFormulas());
        return new PrivateLaboratory(new ArrayList<>(), grid, new ResultsTriangle());
    }
}
