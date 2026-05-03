package edu.advising.pipeline.payment;

import edu.advising.auth.AuthenticationResult;
import edu.advising.model.Payment;
import edu.advising.notifications.ObservableStudent;
import edu.advising.permissions.PermissionTree;

import java.math.BigDecimal;

public class PaymentContext {
    private ObservableStudent student;
    private Payment payment;
    private AuthenticationResult authenticationResult;
    private BigDecimal amount;
    private String paymentType;
    private String paymentMethod;
    private String cardNumber;
    private String expiryDate;
    private String cvv;

    public PaymentContext(ObservableStudent student, BigDecimal amount, String paymentType, String paymentMethod,
                          String cardNumber, String expiryDate, String cvv, AuthenticationResult authenticationResult){
        this.student = student;
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.authenticationResult = authenticationResult;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public ObservableStudent getStudent() {
        return student;
    }

    public AuthenticationResult getAuthenticationResult() {
        return authenticationResult;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCvv() {
        return cvv;
    }
}
