package alchgame.application.dto;

import java.util.List;

/**
 * Informazioni di un giocatore visibili a tutti (hot-seat sicuro): nessun dato
 * privato del laboratorio (ingredienti in mano, griglia di deduzione).
 */
public record PublicPlayerBoardDTO(
        String name,
        int gold,
        List<PotionDTO> potions
) {}
