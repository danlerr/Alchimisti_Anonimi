package alchgame.model;

public class Experiment {

    private final Target     target;
    private final Ingredient ingredient1;
    private final Ingredient ingredient2;
    private final Potion     potion;

    private Experiment(Target target, Ingredient i1, Ingredient i2, Potion potion) {
        this.target      = target;
        this.ingredient1 = i1;
        this.ingredient2 = i2;
        this.potion      = potion;
    }

    public static Experiment createExperiment(Target target,
                                              Ingredient i1,
                                              Ingredient i2,
                                              Potion potion) {
        return new Experiment(target, i1, i2, potion);
    }

    public Target     getTarget()      { return target;      }
    public Ingredient getIngredient1() { return ingredient1; }
    public Ingredient getIngredient2() { return ingredient2; }
    public Potion     getPotion()      { return potion;      }

    @Override
    public String toString() {
        return "Experiment{target=" + target +
               ", i1=" + ingredient1 +
               ", i2=" + ingredient2 +
               ", potion=" + potion + "}";
    }
}
