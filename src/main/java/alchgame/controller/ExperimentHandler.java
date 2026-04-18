package alchgame.controller;

import java.util.List;

import alchgame.model.*;
import alchgame.service.AlchemicAlgorithm;
import alchgame.service.GameContext;

/**
 * ExperimentHandler - UC08 Controller class
 */
public class ExperimentHandler {

    private final GameContext gameContext;
    private final AlchemicAlgorithm alchemicAlgorithm;
    private Target currentTarget;

    public ExperimentHandler(GameContext gameContext, AlchemicAlgorithm alchemicAlgorithm) {
        this.gameContext = gameContext;
        this.alchemicAlgorithm = alchemicAlgorithm;
    }

    // Recupera il target dall'ID fornito e verifica se richiede un pagamento in oro
    // prima di poter condurre l'esperimento. Restituisce true se il pagamento è necessario.
    public boolean paymentCheck(String targetId) {
        this.currentTarget = gameContext.getTarget(targetId);
        return currentTarget.requiresPayment();
    }

    // Restituisce la lista degli ingredienti disponibili nel laboratorio del giocatore corrente.
    public List<Ingredient> getIngredients() {
        return gameContext.getCurrentPlayer().getIngredientsFromLab();
    }

    // Sottrae 1 moneta d'oro al giocatore corrente come costo dell'esperimento.
    public void payGold() {
        Player player = gameContext.getCurrentPlayer();
        player.removeGold(1);
    }

    // Esegue l'esperimento: calcola la pozione risultante dai due ingredienti,
    // registra l'esperimento nel diario del giocatore, applica l'effetto della pozione
    // sul target corrente e restituisce la pozione ottenuta.
    public Potion conductExperiment(Ingredient i1, Ingredient i2) {
        Potion potion = alchemicAlgorithm.computePotion(i1, i2);
        gameContext.getCurrentPlayer().recordExperiment(currentTarget, i1, i2, potion);
        currentTarget.applyEffect(potion);
        return potion;
    }

    // Annulla la selezione del target corrente: il giocatore ha rifiutato di procedere
    // con l'esperimento (es. non vuole pagare o ha cambiato idea).
    public void refuseExperiment() {
        this.currentTarget = null;
    }
}
