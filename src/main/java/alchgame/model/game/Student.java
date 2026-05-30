package alchgame.model.game;

import alchgame.model.alchemy.Potion;

public class Student implements Target {

    private StudentState state;
    private final int costWhenUnhappy;

    public Student(int costWhenUnhappy) {
        this.state = StudentState.HAPPY;
        this.costWhenUnhappy = costWhenUnhappy;
    }

    public StudentState getState() { 
        return state;
    }

    @Override
    public boolean requiresPayment() {
    return this.state == StudentState.UNHAPPY;
    }

    @Override
    public void applyEffect(Potion potion) {
        if (potion.isNegative()) {
            this.state = StudentState.UNHAPPY;
        }
    }
    
    /**
     * Resetta lo stato dello studente a HAPPY a inizio nuovo round.
     * Il costo aggiuntivo per gli esperimenti vale solo nel round in cui
     * lo studente ha ricevuto una pozione negativa.
     */
    @Override
    public void reset() {
        this.state = StudentState.HAPPY;
    }

    @Override
    public String toString() {
        return "Student{state=" + state + "}";
    }

    @Override
    public int getPaymentAmount() {
        return this.state == StudentState.UNHAPPY ? costWhenUnhappy : 0;
    }
}
