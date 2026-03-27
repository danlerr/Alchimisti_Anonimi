package alchgame.model;

public class Student implements Target {

    public enum State { HAPPY, UNHAPPY }

    private State state;

    public Student() {
        this.state = State.HAPPY;
    }

    public State getState()       { return state; }
    public void  setState(State s){ this.state = s; }

    @Override
    public boolean requiresPayment() {
        return false;
    }

    @Override
    public void applyEffect(Potion potion) {
        if (potion.isNegative()) {
            this.state = State.UNHAPPY;
        }
    }

    @Override
    public String toString() {
        return "Student{state=" + state + "}";
    }
}
