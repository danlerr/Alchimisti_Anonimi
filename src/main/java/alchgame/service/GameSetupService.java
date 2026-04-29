package alchgame.service;

import alchgame.model.game.GameStatus;
import alchgame.model.game.GameSession;
import alchgame.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Inizializza una nuova partita creando i giocatori e assegnando le risorse iniziali.
 */
public class GameSetupService {

    private final GameSession alchGame;
    private final PlayerFactory playerFactory;
    private final Random random;
    private final int minPlayers;
    private final int maxPlayers;
    private final int startingGold;
    private final int startingReputation;
    private final int startingActionCubes;
    private final int startingIngredients;

    public GameSetupService(GameSession alchGame,
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

    public void initializePlayers(List<String> names) {
        ensureSetupPhase();
        validatePlayerNames(names);

        List<Player> players = new ArrayList<>();
        for (String name : names) {
            Player player = playerFactory.createPlayer(
                name,
                startingGold,
                startingReputation,
                startingActionCubes);
            players.add(player);
            alchGame.getBoard().dealIngredients(player, startingIngredients);
        }

        alchGame.start(players, random.nextInt(players.size()));
    }

    public int getMinPlayers() { return minPlayers; }
    public int getMaxPlayers() { return maxPlayers; }

    public void validatePlayerNumber(int n) {
        ensureSetupPhase();
        if (n < minPlayers || n > maxPlayers)
            throw new IllegalArgumentException(
                "Numero giocatori non valido: " + n +
                " (consentito " + minPlayers + "–" + maxPlayers + ").");
    }

    public void validatePlayerName(String name, List<String> currentNames, int expectedPlayers) {
        ensureSetupPhase();
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome giocatore vuoto.");
        if (currentNames.contains(name))
            throw new IllegalArgumentException("Nome già usato: " + name);
        if (currentNames.size() >= expectedPlayers)
            throw new IllegalStateException("Tutti i nomi sono già stati inseriti.");
    }

    public void validatePlayerNamesCount(List<String> names, int expectedPlayers) {
        ensureSetupPhase();
        if (names.size() != expectedPlayers)
            throw new IllegalStateException(
                "Nomi mancanti: attesi " + expectedPlayers + ", ricevuti " + names.size() + ".");
    }

    private void ensureSetupPhase() {
        if (alchGame.getLifecycle() != GameStatus.SETUP)
            throw new IllegalStateException("Giocatori inizializzabili solo in fase SETUP.");
    }

    private void validatePlayerNames(List<String> names) {
        if (names == null || names.isEmpty())
            throw new IllegalArgumentException("Lista giocatori vuota.");
        if (names.size() < minPlayers || names.size() > maxPlayers)
            throw new IllegalArgumentException(
                "Numero giocatori non valido: " + names.size() +
                " (consentito " + minPlayers + "–" + maxPlayers + ").");
        long distinct = names.stream().distinct().count();
        if (distinct != names.size())
            throw new IllegalArgumentException("Nomi giocatori duplicati.");
    }
}
