package com.assignment.rewardsapi.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.assignment.rewardsapi.model.Transaction;
import com.assignment.rewardsapi.repository.TransactionRepository;

@Service
public class RewardsService {

    private final TransactionRepository transactionRepository;

    private final Map<String, List<Transaction>> inMemoryTransactions = new HashMap<>();

    public RewardsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    //Validate customer id
    public boolean isValidCustomerId(String value) {
        // Check for null or length less than 5
        if (value == null || value.length() != 7) {
            return false;
        }

        // Check if it starts with "CUST" and the rest are digits
        return value.startsWith("CUST") && value.substring(4).matches("\\d+");
    }

    //Record transaction in database
    public void recordTransaction(Transaction transaction) {
        if (transactionRepository != null) {
            transactionRepository.save(transaction);
        } else {
            inMemoryTransactions
                .computeIfAbsent(transaction.getCustomerId(), k -> new ArrayList<>())
                .add(transaction);
        }
    }

    // Updated method to accept fromDate and toDate
    public List<Transaction> getTransactionsByCustomerId(String customerId, LocalDateTime fromDate, LocalDateTime toDate) {
        if (transactionRepository != null) {
            return transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, fromDate, toDate);
        }
        return inMemoryTransactions.getOrDefault(customerId, new ArrayList<>())
                .stream()
                .filter(tx -> !tx.getTransactionDate().isBefore(fromDate) && !tx.getTransactionDate().isAfter(toDate))
                .collect(Collectors.toList());
    }
    
    //Calculate reward points based on purchase amount
    private int calculatePoints(double amount) {
        int points = 0;
        if (amount > 100) {
            points += (int) ((amount - 100) * 2); // 2 points over $100
            points += 50; // 1 point for $50–$100
        } else if (amount > 50) {
            points += (int) (amount - 50); // 1 point for $50–amount
        }
        return points;
    }

    //Calculate monthly points and transaction count and returns a map
    public Map<String, Map<String, Integer>> calculateMonthlyPointsAndTransactionCount(String customerId, List<Transaction> transactions) {
        Map<String, Map<String, Integer>> monthlyData = new HashMap<>();

        for (Transaction transaction : transactions) {
            LocalDate transactionDate = transaction.getTransactionDate().toLocalDate();
            int points = calculatePoints(transaction.getPurchaseAmount());
            String monthKey = transactionDate.getMonth().toString();

            // Get or create the inner map for the month
            Map<String, Integer> monthData = monthlyData.computeIfAbsent(monthKey, k -> new HashMap<>());

            // Update points and counts
            monthData.put("points", monthData.getOrDefault("points", 0) + points);
            monthData.put("transactionCount", monthData.getOrDefault("transactionCount", 0) + 1);
        }
        return monthlyData;
    }

    //Calculate total points based on the over all transactions
    public int calculateTotalPoints(String customerId, List<Transaction> transactions) {
        return transactions.stream()
                .mapToInt(tx -> calculatePoints(tx.getPurchaseAmount()))
                .sum();
    }
}
