package com.assignment.rewardsapi.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.assignment.rewardsapi.exception.CustomerNotFoundException;
import com.assignment.rewardsapi.model.Transaction;
import com.assignment.rewardsapi.service.RewardsService;
import com.google.gson.Gson;

@Controller
@RequestMapping("/api/rewards")
public class RewardsController {

	private static final Logger log = LogManager.getLogger(RewardsService.class);
	private final Gson gson = new Gson();
	
	@Autowired
	private RewardsService rewardsService;

	// This method returns jsp name to render landing page
	@GetMapping("/")
	public String getTransactionsPage() {
		return "getTransactions";
	}

	// post transaction details of the customer to database
	@PostMapping("/transaction")
	public ResponseEntity<?> recordTransaction(@Valid @RequestBody Transaction transaction) {
		try {
			log.info("Recoding customer Transaction");
			rewardsService.recordTransaction(transaction);
			log.info("Transaction recorded successfully with the data : " + gson.toJson(transaction));
			return new ResponseEntity<>("Transaction recorded successfully", HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			log.error("Exception with the data : " + gson.toJson(transaction));
			return new ResponseEntity<>("Invalid transaction data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("Exception with the data : " + gson.toJson(transaction));
			return new ResponseEntity<>("An error occurred while recording transaction.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// calculate reward points based on customer id within given date range.
	@GetMapping("/customer/{customerId}")
	public ResponseEntity<?> getCustomerRewards(@PathVariable String customerId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		try {
			log.info("Fetching customer Transaction with customerId: " + customerId);
			if (!rewardsService.isValidCustomerId(customerId)) {
				log.error("Validation failure with customerId: " + customerId);
				return ResponseEntity.badRequest().body("Invalid Customer Id!");
			}
			if (fromDate.isAfter(toDate)) {
				log.error("Validation failure for date with fromDate: " + fromDate + " and toDate: " + toDate);
				return ResponseEntity.badRequest().body("Invalid Date Inputs!");
			}

			LocalDateTime from = fromDate.atStartOfDay();
			LocalDateTime to = toDate.atStartOfDay();

			List<Transaction> transactions = rewardsService.getTransactionsByCustomerId(customerId, from, to);

			if (transactions == null || transactions.isEmpty()) {
				log.error("No transactions found for customerId: " + customerId
						+ " within the specified date range: " + fromDate + " - " + toDate);
				throw new CustomerNotFoundException("No transactions found for customerId: " + customerId
						+ " within the specified date range: " + fromDate + " - " + toDate);
			}

			Map<String, Map<String, Integer>> monthlyData = rewardsService
					.calculateMonthlyPointsAndTransactionCount(customerId, transactions);
			int totalPoints = rewardsService.calculateTotalPoints(customerId, transactions);

			Map<String, Object> response = new HashMap<>();
			response.put("customerId", customerId);
			response.put("monthlyData", monthlyData);
			response.put("totalPoints", totalPoints);
			log.info("Customer Reward Response: " + gson.toJson(response));
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (CustomerNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Exception while calculating customer reward points with customerId: " + customerId);
			return new ResponseEntity<>("An error occurred while fetching rewards! Please, try again ",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
