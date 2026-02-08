package alchgame.application;

import alchgame.model.*;

/**
 * Aggregates game-related domain objects for a single player's game context.
 * Applies INDIRECTION and reduces LOW COUPLING by grouping related dependencies.
 * This is a PURE FABRICATION that helps manage complexity.
 */
public class GameContext {
    
    private final PrivateLaboratory laboratory;
    private final PublicPlayerBoard playerBoard;
    private final Student student;
    private final ResultsTriangle resultsTriangle;
    private final DeductionGrid deductionGrid;
    
    /**
     * Creates a game context with all necessary components.
     * 
     * @param laboratory the player's private laboratory
     * @param playerBoard the player's public board
     * @param student the student for experiments
     * @param resultsTriangle the results triangle
     * @param deductionGrid the deduction grid
     */
    public GameContext(
            PrivateLaboratory laboratory,
            PublicPlayerBoard playerBoard,
            Student student,
            ResultsTriangle resultsTriangle,
            DeductionGrid deductionGrid) {
        
        if (laboratory == null) {
            throw new IllegalArgumentException("Laboratory cannot be null");
        }
        if (playerBoard == null) {
            throw new IllegalArgumentException("PlayerBoard cannot be null");
        }
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (resultsTriangle == null) {
            throw new IllegalArgumentException("ResultsTriangle cannot be null");
        }
        if (deductionGrid == null) {
            throw new IllegalArgumentException("DeductionGrid cannot be null");
        }
        
        this.laboratory = laboratory;
        this.playerBoard = playerBoard;
        this.student = student;
        this.resultsTriangle = resultsTriangle;
        this.deductionGrid = deductionGrid;
    }
    
    public PrivateLaboratory getLaboratory() {
        return laboratory;
    }
    
    public PublicPlayerBoard getPlayerBoard() {
        return playerBoard;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public ResultsTriangle getResultsTriangle() {
        return resultsTriangle;
    }
    
    public DeductionGrid getDeductionGrid() {
        return deductionGrid;
    }
}
