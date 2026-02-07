package alchgame.service;

import alchgame.model.AlchemicFormula;
import alchgame.model.Atom;
import alchgame.model.Ingredient;
import alchgame.model.Potion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlchemicAlgorithm {
    private static final String[] COLORS = {"red", "green", "blue"};
    private static final String SIGN_POSITIVE = "+";
    private static final String SIGN_NEGATIVE = "-";

    private final Map<String, AlchemicFormula> ingredientFormulas = new HashMap<>();
    private final List<AlchemicFormula> formulaPool = new ArrayList<>();

    public AlchemicAlgorithm() {
        buildDefaultFormulaPool();
        buildDefaultMapping();
    }

    public Potion computePotion(Ingredient ingredient1, Ingredient ingredient2) {
        AlchemicFormula formula1 = resolveFormula(ingredient1);
        AlchemicFormula formula2 = resolveFormula(ingredient2);

        String matchingColor = null;
        String matchingSign = null;

        for (String color : COLORS) {
            String sign1 = formula1.getSignForColor(color);
            String sign2 = formula2.getSignForColor(color);

            if (sign1 != null && sign1.equals(sign2)) {
                if (matchingColor == null) {
                    matchingColor = color;
                    matchingSign = sign1;
                }
            }
        }

        if (matchingColor == null) {
            return new Potion("neutral", "0");
        }

        return new Potion(matchingColor, matchingSign);
    }

    private AlchemicFormula resolveFormula(Ingredient ingredient) {
        String name = ingredient == null ? "" : ingredient.getName();
        String key = name.trim().toLowerCase();

        if (ingredientFormulas.containsKey(key)) {
            return ingredientFormulas.get(key);
        }

        int index = Math.abs(key.hashCode()) % formulaPool.size();
        AlchemicFormula formula = formulaPool.get(index);
        ingredientFormulas.put(key, formula);
        return formula;
    }

    private void buildDefaultFormulaPool() {
        formulaPool.add(formulaOf(SIGN_POSITIVE, SIGN_POSITIVE, SIGN_POSITIVE));
        formulaPool.add(formulaOf(SIGN_POSITIVE, SIGN_POSITIVE, SIGN_NEGATIVE));
        formulaPool.add(formulaOf(SIGN_POSITIVE, SIGN_NEGATIVE, SIGN_POSITIVE));
        formulaPool.add(formulaOf(SIGN_POSITIVE, SIGN_NEGATIVE, SIGN_NEGATIVE));
        formulaPool.add(formulaOf(SIGN_NEGATIVE, SIGN_POSITIVE, SIGN_POSITIVE));
        formulaPool.add(formulaOf(SIGN_NEGATIVE, SIGN_POSITIVE, SIGN_NEGATIVE));
        formulaPool.add(formulaOf(SIGN_NEGATIVE, SIGN_NEGATIVE, SIGN_POSITIVE));
        formulaPool.add(formulaOf(SIGN_NEGATIVE, SIGN_NEGATIVE, SIGN_NEGATIVE));
    }

    private void buildDefaultMapping() {
        assignFormula("toad", formulaPool.get(0));
        assignFormula("mushroom", formulaPool.get(1));
        assignFormula("mandrake", formulaPool.get(2));
        assignFormula("scorpion", formulaPool.get(3));
        assignFormula("fern", formulaPool.get(4));
        assignFormula("bird claw", formulaPool.get(5));
        assignFormula("root", formulaPool.get(6));
        assignFormula("flower", formulaPool.get(7));
    }

    private void assignFormula(String ingredientName, AlchemicFormula formula) {
        ingredientFormulas.put(ingredientName.toLowerCase(), formula);
    }

    private AlchemicFormula formulaOf(String redSign, String greenSign, String blueSign) {
        return new AlchemicFormula(List.of(
                new Atom("red", "", redSign),
                new Atom("green", "", greenSign),
                new Atom("blue", "", blueSign)
        ));
    }
}
