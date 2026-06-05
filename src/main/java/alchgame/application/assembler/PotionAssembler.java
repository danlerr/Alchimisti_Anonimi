package alchgame.application.assembler;

import alchgame.application.dto.PotionDTO;
import alchgame.model.alchemy.Potion;

public class PotionAssembler {

    public static PotionDTO toDTO(Potion potion) {
        String label     = potion.isNeutral() ? "NEUTRA"
                         : potion.getColor().name() + " " + potion.getSign().name();
        String colorName = potion.isNeutral() ? "NEUTRAL" : potion.getColor().name();
        return new PotionDTO(label, colorName);
    }
}
