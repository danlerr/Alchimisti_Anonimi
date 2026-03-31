package alchgame.model;


public class Student implements Target {

    private StudentState state;

    public Student() {
        this.state = StudentState.HAPPY;
    }

    public StudentState getState()       { return state; }
    public void  setState(StudentState s){ this.state = s; }

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

    @Override
    public String toString() {
        return "Student{state=" + state + "}";
    }
}
