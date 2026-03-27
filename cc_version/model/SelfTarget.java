package alchgame.model;

/**
 * Represents a self-experiment (testing on oneself).
 * Applies POLYMORPHISM principle - different behavior from StudentTarget.
 */
public class SelfTarget implements ExperimentTarget {
    
    @Override
    public boolean requiresPayment() {
        // Self-experiments are always free
        return false;
    }
    
    @Override
    public void applyConsequences(Potion potion, Player player) {
        // Self-experiments typically have no immediate consequences
        // This can be extended in the future for different game rules.          METTERE QUI LE CONSEGUENZE DELLE POZIONI NEGATIVE SU 'ME STESSO'
    }
    
    @Override
    public String getDescription() {
        return "Self-experiment (testing on yourself)";
    }
    
    @Override
    public TargetType getType() {
        return TargetType.SELF;
    }
}
