package alchgame.service;

import alchgame.model.game.AlchGame;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Servizio applicativo del caso d'uso "Iniziare partita".
 *
 * Valida i dati iniziali, crea i giocatori, assegna le risorse iniziali
 * e avvia la partita delegando ad AlchGame.
 */
public class StartGameService {

    private final AlchGame alchGame;
    private final PlayerFactory playerFactory;
    private final Random random;
    private final int minPlayers;
    private final int maxPlayers;
    private final int startingGold;
    private final int startingReputation;
    private final int startingActionCubes;
    private final int startingIngredients;

    public StartGameService(AlchGame alchGame,
                            PlayerFactory playerFactory,
                            Random random,
                            int minPlayers,
                            int maxPlayers,
                            int startingGold,
                            int startingReputation,
                            int startingActionCubes,
                            int startingIngredients) {
        this.alchGame = alchGame;
        this.playerFactory = playerFactory;
        this.random = random;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.startingGold = startingGold;
        this.startingReputation = startingReputation;
        this.startingActionCubes = startingActionCubes;
        this.startingIngredients = startingIngredients;
    }

    public void startGame(List<String> names) {
        ensureSetupPhase();
        validatePlayerList(names);

        List<Player> players = createPlayers(names);
        alchGame.start(players, random.nextInt(players.size()));
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void validatePlayerNumber(int n) {
        ensureSetupPhase();

        if (n < minPlayers || n > maxPlayers) {
            throw new IllegalArgumentException(
                    "Numero giocatori non valido: " + n +
                    " (consentito " + minPlayers + "-" + maxPlayers + ")."
            );
        }
    }

    public void validatePlayerName(String name, List<String> currentNames) {
        ensureSetupPhase();

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome giocatore vuoto.");
        }

        if (currentNames.contains(name)) {
            throw new IllegalArgumentException("Nome già usato: " + name);
        }
    }

    private void validatePlayerList(List<String> names) {
        if (names == null || names.isEmpty()) {
            throw new IllegalArgumentException("Lista giocatori vuota.");
        }

        if (names.size() < minPlayers || names.size() > maxPlayers) {
            throw new IllegalArgumentException(
                    "Numero giocatori non valido: " + names.size() +
                    " (consentito " + minPlayers + "-" + maxPlayers + ")."
            );
        }

        long distinct = names.stream().distinct().count();

        if (distinct != names.size()) {
            throw new IllegalArgumentException("Nomi giocatori duplicati.");
        }
    }

    private List<Player> createPlayers(List<String> names) {
        List<Player> players = new ArrayList<>();

        for (String name : names) {
            Player player = playerFactory.createPlayer(
                    name,
                    startingGold,
                    startingReputation,
                    startingActionCubes
            );

            players.add(player);
            alchGame.getBoard().dealIngredients(player, startingIngredients);
        }

        return players;
    }

    private void ensureSetupPhase() {
        if (alchGame.hasStarted()) {
            throw new IllegalStateException("Giocatori inizializzabili solo in fase SETUP.");
        }
    }
}
