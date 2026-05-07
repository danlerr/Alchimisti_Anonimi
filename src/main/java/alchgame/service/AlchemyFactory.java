package alchgame.service;

import alchgame.model.alchemy.AlchemicAlgorithm;
import alchgame.model.alchemy.AlchemicFormula;
import alchgame.model.alchemy.AlchemicMapping;
import alchgame.model.alchemy.Atom;
import alchgame.model.alchemy.Color;
import alchgame.model.alchemy.Ingredient;
import alchgame.model.alchemy.Sign;
import alchgame.model.alchemy.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Factory responsabile della costruzione del sottosistema alchemico.
 *
 * Interpreta la sezione alchemica della configurazione e crea ingredienti,
 * formule, mapping segreto ingrediente-formula e algoritmo di calcolo delle
 * pozioni. Mantiene fuori dal bootstrapper i dettagli di parsing delle formule.
 */
public class AlchemyFactory {

    private final Properties props;

    public AlchemyFactory(Properties props) {
        this.props = props;
    }

    public List<Ingredient> createIngredients() {
        return createIngredientNames().stream()
                .map(Ingredient::new)
                .toList();
    }

    public List<AlchemicFormula> createFormulas() {
        int count = Integer.parseInt(props.getProperty("formula.count"));
        List<AlchemicFormula> formulas = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            formulas.add(createFormula(i));
        }

        return List.copyOf(formulas);
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

    private List<String> createIngredientNames() {
        return List.of(props.getProperty("ingredients").split(","));
    }

    private AlchemicFormula createFormula(int index) {
        List<Atom> atoms = new ArrayList<>();

        for (Color color : Color.real()) {
            Size size = Size.valueOf(props.getProperty("formula." + index + "." + color.name() + ".size"));
            Sign sign = Sign.valueOf(props.getProperty("formula." + index + "." + color.name() + ".sign"));
            atoms.add(new Atom(color, size, sign));
        }

        return new AlchemicFormula(atoms);
    }
}
