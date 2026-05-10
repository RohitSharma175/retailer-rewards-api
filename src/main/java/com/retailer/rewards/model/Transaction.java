package com.retailer.rewards.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a single purchase transaction made by a customer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private String transactionId;
    private String customerId;
    private String customerName;
    private LocalDate transactionDate;
    private double amount;
}
