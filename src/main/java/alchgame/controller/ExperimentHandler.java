package alchgame.controller;

import alchgame.model.*;
import alchgame.service.AlchemicAlgorithm;
import alchgame.service.GameContext;

import java.util.List;

/**
 * ExperimentHandler — controller che orchestra i tre casi d'uso:
 *
 *   1. startExperiment(targetId)   → SD [iniziaEsperimento]
 *   2. pagaOro()                   → SD [pagaOro]
 *   3. conductExperiment(i1, i2)   → SD [ConductExperiment]
 */
public class ExperimentHandler {

    private final GameContext       gameContext;
    private final AlchemicAlgorithm alchemicAlgorithm;

    /** Target selezionato in startExperiment, usato in conductExperiment. */
    private Target currentTarget;

    public ExperimentHandler(GameContext gameContext, AlchemicAlgorithm alchemicAlgorithm) {
        this.gameContext       = gameContext;
        this.alchemicAlgorithm = alchemicAlgorithm;
    }

    // =========================================================================
    // SD [iniziaEsperimento]
    // =========================================================================

    public Object startExperiment(String targetId) {

        // 1.1–1.2: recupera il Target
        this.currentTarget = gameContext.getTarget(targetId);

        // 1.3–1.4: recupera il Player corrente
        Player player = gameContext.getCurrentPlayer();

        // 1.5–1.6: verifica se richiede pagamento
        boolean payment = currentTarget.requiresPayment();

        if (payment) {
            // 1.7: la UI chiamerà pagaOro()
            return new PaymentRequest();
        } else {
            // 1.8–1.9
            PrivateLaboratory lab = player.getPrivateLaboratory();
            // 1.10–1.11
            List<Ingredient> ingredients = lab.getIngredients();
            // 1.12
            return new IngredientsRequest(ingredients);
        }
    }

    // =========================================================================
    // SD [pagaOro]
    // =========================================================================

    public IngredientsRequest pagaOro() {

        // 1.1–1.2
        Player player = gameContext.getCurrentPlayer();

        // 1.3–1.4: rimuove 1 moneta d'oro
        boolean success = player.removeGold(1);
        if (!success) throw new IllegalStateException("Oro insufficiente.");

        // 1.5
        PrivateLaboratory privateLaboratory = player.getPrivateLaboratory();

        // 1.5.1–1.5.1.1
        List<Ingredient> ingredients = privateLaboratory.getIngredients();

        // 1.5.1.1.1
        return new IngredientsRequest(ingredients);
    }

    // =========================================================================
    // SD [ConductExperiment]
    // =========================================================================

    public void conductExperiment(Ingredient ingredient1, Ingredient ingredient2) {

        // 1.1 / 1.1.1–2: calcola Potion tramite AlchemicAlgorithm
        Potion potion = alchemicAlgorithm.computePotion(ingredient1, ingredient2);

        // 2.1.1–3: crea Experiment
        Experiment experiment = Experiment.createExperiment(
                currentTarget, ingredient1, ingredient2, potion);

        Player player = gameContext.getCurrentPlayer();

        // 3.1: pubblica su PublicPlayerBoard
        player.getPublicPlayerBoard().publishExperimentResult(potion);

        // 3.2: aggiorna PrivateLaboratory (discard → addObservation → recordResult)
        player.getPrivateLaboratory().updatePrivateLab(ingredient1, ingredient2, potion);

        // 3.3–3.4: applica effetto sul target
        //   Student + negativa → UNHAPPY | Player + negativa → malus reputation
        currentTarget.applyEffect(potion);

        player.addExperiment(experiment);

        // 3.5
        showResult(experiment);
    }

    // =========================================================================
    // Utility
    // =========================================================================

    private void showResult(Experiment experiment) {
        System.out.println("[ExperimentHandler] Risultato: " + experiment);
    }
}
