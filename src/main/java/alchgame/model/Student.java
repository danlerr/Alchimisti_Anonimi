package alchgame.model;

public class Student {
    private StudentStatus status;

    public Student(StudentStatus status) {
        this.status = status;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }
}
