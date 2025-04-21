package com.assignment.rewardsapi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
	
    private String customerId;
    private String transactionId;
    private double amount;
    private String transactionDate;

}

