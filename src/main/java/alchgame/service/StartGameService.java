package alchgame.service;

import alchgame.model.factory.PlayerFactory;
import alchgame.model.game.AlchGame;
import alchgame.model.player.Player;

import java.util.List;
import java.util.Random;

public class StartGameService {

    private final AlchGame alchGame;
    private final PlayerFactory playerFactory;
    private final Random random;

    public StartGameService(AlchGame alchGame, PlayerFactory playerFactory, Random random) {
        this.alchGame = alchGame;
        this.playerFactory = playerFactory;
        this.random = random;
    }

    public void startGame(List<String> names) {
        List<Player> players = names.stream()
            .map(name -> playerFactory.createPlayer(
                name,
                alchGame.getStartingGold(),
                alchGame.getStartingReputation(),
                alchGame.getStartingActionCubes()))
            .toList();

        players.forEach(p -> alchGame.getBoard().dealIngredients(p, alchGame.getStartingIngredients()));

        alchGame.start(players, random.nextInt(players.size()));
    }

    public void validatePlayerNumber(int n) { 
        alchGame.validatePlayerNumber(n);
    }
    public void validatePlayerName(String name, List<String> names) {
        alchGame.validatePlayerName(name, names); 
    }
    
    public int getMinPlayers() { 
        return alchGame.getMinPlayers();
    }

    public int getMaxPlayers() {
        return alchGame.getMaxPlayers(); 
    }

}