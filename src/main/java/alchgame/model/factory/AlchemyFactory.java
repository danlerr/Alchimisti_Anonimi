package alchgame.model.factory;

import alchgame.config.GameConfig;
import alchgame.model.alchemy.AlchemicAlgorithm;
import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.alchemy.AlchemicMapping;
import alchgame.model.alchemy.Atom;
import alchgame.model.alchemy.Color;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.Sign;
import alchgame.model.alchemy.Size;
import alchgame.model.alchemy.potionEffect.BlueNegativeEffect;
import alchgame.model.alchemy.potionEffect.BluePositiveEffect;
import alchgame.model.alchemy.potionEffect.GreenNegativeEffect;
import alchgame.model.alchemy.potionEffect.GreenPositiveEffect;
import alchgame.model.alchemy.potionEffect.PotionEffectRegistry;
import alchgame.model.alchemy.potionEffect.RedNegativeEffect;
import alchgame.model.alchemy.potionEffect.RedPositiveEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory responsabile della costruzione del sottosistema alchemico.
 *
 * Riceve dati grezzi dalla configurazione e crea gli oggetti del dominio
 * alchemico: ingredienti, formule, mapping segreto ingrediente-formula e
 * algoritmo di calcolo delle pozioni.
 */
public class AlchemyFactory {

    public List<Ingredient> createIngredients(List<String> ingredientNames) {
        return ingredientNames.stream()
                .map(Ingredient::new)
                .toList();
    }

    public List<AlchemicFormula> createFormulas(List<FormulaSpec> formulaSpecs) {
        return formulaSpecs.stream()
                .map(this::createFormula)
                .toList();
    }

    public AlchemicMapping createRandomMapping(
            List<Ingredient> ingredients,
            List<AlchemicFormula> formulas
    ) {
        List<AlchemicFormula> shuffled = new ArrayList<>(formulas);
        Collections.shuffle(shuffled);

        Map<Ingredient, AlchemicFormula> rawMapping = new HashMap<>();

        for (int i = 0; i < ingredients.size(); i++) {
            rawMapping.put(ingredients.get(i), shuffled.get(i));
        }

        return new AlchemicMapping(rawMapping);
    }

    public AlchemicAlgorithm createAlgorithm(AlchemicMapping mapping) {
        return new AlchemicAlgorithm(mapping);
    }

    private AlchemicFormula createFormula(FormulaSpec formulaSpec) {
        List<Atom> atoms = formulaSpec.atoms().stream()
                .map(this::createAtom)
                .toList();

        return new AlchemicFormula(atoms);
    }

    private Atom createAtom(AtomSpec atomSpec) {
        return new Atom(
                Color.valueOf(atomSpec.color()),
                Size.valueOf(atomSpec.size()),
                Sign.valueOf(atomSpec.sign())
        );
    }

    /**
     * Costruisce il registro degli effetti delle pozioni, istanziando le 
     * strategie e iniettando al loro interno i valori di bilanciamento.
     */
    public static PotionEffectRegistry createRegistry() {
        PotionEffectRegistry registry = new PotionEffectRegistry();
        
        // --- EFFETTI NEGATIVI ---
        registry.register(Color.BLUE,  Sign.NEGATIVE, new BlueNegativeEffect(GameConfig.EFFECT_BLUE_NEG));
        registry.register(Color.RED,   Sign.NEGATIVE, new RedNegativeEffect(GameConfig.EFFECT_RED_NEG));
        registry.register(Color.GREEN, Sign.NEGATIVE, new GreenNegativeEffect());

        // --- EFFETTI POSITIVI ---
        registry.register(Color.BLUE,  Sign.POSITIVE, new BluePositiveEffect(GameConfig.EFFECT_BLUE_POS));
        registry.register(Color.RED,   Sign.POSITIVE, new RedPositiveEffect(GameConfig.EFFECT_RED_POS));
        registry.register(Color.GREEN, Sign.POSITIVE, new GreenPositiveEffect(GameConfig.EFFECT_GREEN_POS));
        
        return registry;
    }
}
