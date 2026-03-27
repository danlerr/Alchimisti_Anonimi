package alchgame.application;

/**
 * Represents the result of a payment attempt.
 * Applies INFORMATION EXPERT - encapsulates payment outcome details.
 */
public class PaymentResult {
    
    private final boolean success;
    private final String message;
    private final int amountPaid;
    private final PaymentStatus status;
    
    private PaymentResult(boolean success, String message, int amountPaid, PaymentStatus status) {
        this.success = success;
        this.message = message;
        this.amountPaid = amountPaid;
        this.status = status;
    }
    
    public static PaymentResult success(int amount) {
        return new PaymentResult(
            true, 
            "Payment of " + amount + " gold successful", 
            amount,
            PaymentStatus.PAID
        );
    }
    
    public static PaymentResult notRequired() {
        return new PaymentResult(
            true, 
            "Payment not required", 
            0,
            PaymentStatus.NOT_REQUIRED
        );
    }
    
    public static PaymentResult insufficientFunds(int required, int available) {
        return new PaymentResult(
            false, 
            "Insufficient gold: need " + required + " but have " + available, 
            0,
            PaymentStatus.INSUFFICIENT_FUNDS
        );
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getAmountPaid() {
        return amountPaid;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public enum PaymentStatus {
        PAID,
        NOT_REQUIRED,
        INSUFFICIENT_FUNDS
    }
}
