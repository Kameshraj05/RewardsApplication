package com.assignment.rewardsapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    @NotNull
    private String customerId;
    

    @NotNull
    @Positive
    private Double purchaseAmount;

    @NotNull
    private LocalDateTime transactionDate;

    public Transaction() {
    }

    public Transaction(String customerId, Double purchaseAmount, LocalDateTime transactionDate) {
        this.customerId = customerId;
        this.purchaseAmount = purchaseAmount;
        this.transactionDate = transactionDate;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(Double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
