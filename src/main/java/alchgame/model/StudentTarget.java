package alchgame.model;

/**
 * Represents an experiment conducted on a student.
 * Applies POLYMORPHISM principle - different behavior based on student status.
 * Applies INFORMATION EXPERT - Student knows its own status.
 */
public class StudentTarget implements ExperimentTarget {
    
    private final Student student;
    
    public StudentTarget(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        this.student = student;
    }
    
    @Override
    public boolean requiresPayment() {
        // INFORMATION EXPERT: Student knows if it's unhappy
        return student.getStatus() == StudentStatus.UNHAPPY;
    }
    
    @Override
    public void applyConsequences(Potion potion, Player player) {
        // Update student status based on potion result
        if (potion.isPositive()) {
            student.setStatus(StudentStatus.HAPPY);
        } else if (potion.isNegative()) {
            student.setStatus(StudentStatus.UNHAPPY);
        }
        // Neutral potions don't change student status
    }
    
    @Override
    public String getDescription() {
        return "Experiment on student (Status: " + student.getStatus() + ")";
    }
    
    @Override
    public TargetType getType() {
        return TargetType.STUDENT;
    }
    
    public Student getStudent() {
        return student;
    }
}
