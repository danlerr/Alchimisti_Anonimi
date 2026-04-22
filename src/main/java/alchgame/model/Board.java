package alchgame.model;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Board — tabellone di gioco. Contiene gli spazi azione, lo SpazioOrdine
 * (tracciato di risveglio) e i mazzi di ingredienti e carte favore.
 */
public class Board {

    private final Map<String, ActionSpace> actionSpaces;
    private final SpazioOrdine spazioOrdine;
    private final Deque<Ingredient> ingredientDeck;
    private final Deque<FavorCard> favorDeck;

    public Board(Map<String, ActionSpace> actionSpaces,
                 SpazioOrdine spazioOrdine,
                 Deque<Ingredient> ingredientDeck,
                 Deque<FavorCard> favorDeck) {
        this.actionSpaces = new HashMap<>(actionSpaces);
        this.spazioOrdine = spazioOrdine;
        this.ingredientDeck = ingredientDeck;
        this.favorDeck = favorDeck;
    }

    public ActionSpace getActionSpace(String actionSpaceId) {
        ActionSpace space = actionSpaces.get(actionSpaceId);
        if (space == null)
            throw new IllegalArgumentException("Spazio azione non trovato: " + actionSpaceId);
        return space;
    }

    /**
     * Delega la dichiarazione dell'azione allo spazio azione corrispondente.
     */
    public void setAction(String actionSpaceId, Player player) {
        getActionSpace(actionSpaceId).setAction(actionSpaceId, player);
    }

    // ---- SpazioOrdine -------------------------------------------------------

    public void setPlayer(String slotID, Player player) {
        spazioOrdine.setPlayer(slotID, player);
    }

    public Resources getResources(String slotID) {
        return spazioOrdine.getResources(slotID);
    }

    public void pickCard(Player player, Resources resources) {
        updateDeck(resources);
        for (int i = 0; i < resources.ingredientCount(); i++) {
            Ingredient ingredient = ingredientDeck.poll();
            if (ingredient == null)
                throw new IllegalStateException("Mazzo ingredienti esaurito.");
            player.addIngredient(ingredient);
        }
        for (int i = 0; i < resources.favorCount(); i++) {
            FavorCard favor = favorDeck.poll();
            if (favor == null)
                throw new IllegalStateException("Mazzo favori esaurito.");
            player.addFavor(favor);
        }
    }

    private void updateDeck(Resources resources) {
        if (ingredientDeck.size() < resources.ingredientCount())
            throw new IllegalStateException("Mazzo ingredienti insufficiente.");
        if (favorDeck.size() < resources.favorCount())
            throw new IllegalStateException("Mazzo favori insufficiente.");
    }
}
