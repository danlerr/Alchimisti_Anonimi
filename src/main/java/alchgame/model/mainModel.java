package alchgame.model;

import alchgame.model.game.AlchGame;
import alchgame.model.game.Round;
import alchgame.model.game.Student;
import alchgame.model.board.Board;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.AlchemicFormula;
import alchgame.service.AlchemyFactory;
import alchgame.service.BoardFactory;
import alchgame.service.PlayerFactory;
import alchgame.service.StartGameService;
import alchgame.resources.GameConfig;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class mainModel {
    public static void main(String[] args) {
        
        // =====================================================================
        // FASE 1: SETUP DEL GIOCO
        // =====================================================================
        AlchemyFactory alchemyFactory = new AlchemyFactory();
        List<Ingredient> ingredients = alchemyFactory.createIngredients(GameConfig.getIngredientNames());
        List<AlchemicFormula> formulas = alchemyFactory.createFormulas(GameConfig.getFormulaSpecs());

        BoardFactory boardFactory = new BoardFactory(
                GameConfig.getSlotSpecs(),
                GameConfig.ACTION_ORDER,
                GameConfig.INGREDIENT_DECK_COPIES,
                GameConfig.FAVOR_DECK_SIZE
        );
        Board board = boardFactory.createBoard(ingredients);

        AlchGame game = new AlchGame(
                board,
                GameConfig.STARTING_ACTION_CUBES,
                GameConfig.TOTAL_ROUNDS,
                Map.of(GameConfig.TARGET_STUDENT_ID, new Student(GameConfig.STUDENT_UNHAPPY_COST)),
                GameConfig.SELF_ID
        );

        PlayerFactory playerFactory = new PlayerFactory(ingredients, formulas);
        StartGameService startService = new StartGameService(
                game, playerFactory, new Random(),
                GameConfig.MIN_PLAYERS, GameConfig.MAX_PLAYERS,
                GameConfig.STARTING_GOLD, GameConfig.STARTING_REPUTATION,
                GameConfig.STARTING_ACTION_CUBES, GameConfig.STARTING_INGREDIENTS
        );

        // Avviamo la partita con 2 giocatori
        startService.startGame(List.of("Alice", "Bob"));

        // =====================================================================
        // FASE 2: IL GAME LOOP COMPLETO (Simulazione)
        // =====================================================================
        System.out.println("=== INIZIO PARTITA (Totale Round: " + game.getTotalRounds() + ") ===");

        // Il ciclo gira finché la partita non decreta la fine!
        while (!game.isOver()) {
            Round round = game.getCurrentRound();
            
            System.out.println("\n--- INIZIO ROUND " + game.getCurrentRoundNumber() + " ---");

            // FASE 1: ORDER
            while(round.getPhase() == Round.Phase.ORDER) {
                System.out.println("[ORDER] Tocca a: " + round.getCurrentPlayer().getName());
                round.nextPlayer();
            }

            // FASE 2: DECLARATION
            while(round.getPhase() == Round.Phase.DECLARATION) {
                System.out.println("[DECLARATION] Tocca a: " + round.getCurrentPlayer().getName());
                round.nextPlayer();
            }

            // FASE 3: RESOLUTION
            while(round.getPhase() == Round.Phase.RESOLUTION) {
                System.out.println("[RESOLUTION] Azione " + round.getCurrentResolutionActionId() + " - tocca a: " + round.getCurrentPlayer().getName());
                round.nextPlayer();
            }

            // FINE ROUND: Facciamo avanzare il gioco al round successivo
            if (round.isRoundOver()) {
                System.out.println(">>> ROUND " + game.getCurrentRoundNumber() + " CONCLUSO <<<");
                
                // Chiediamo al gioco di avanzare (questo pulirà il tabellone e creerà un nuovo Round)
                // Usiamo un if per assicurarci di non chiamarlo dopo l'ultimo round
                if (!game.isOver()) {
                    game.nextRound();
                }
            }
        }

        System.out.println("\n=== PARTITA TERMINATA CON SUCCESSO! ===");
    }
}