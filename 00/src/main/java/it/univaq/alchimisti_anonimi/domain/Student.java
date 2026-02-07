package it.univaq.alchimisti_anonimi.domain;

import java.util.Objects;

public class Student {
    private StudentState state;

    public Student(StudentState state) {
        this.state = Objects.requireNonNull(state);
    }

    public StudentState getState() {
        return state;
    }

    public void setState(StudentState state) {
        this.state = Objects.requireNonNull(state);
    }
}
