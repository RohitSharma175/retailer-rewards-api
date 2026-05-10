package com.retailer.rewards.controller;

import com.retailer.rewards.model.CustomerRewardSummary;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.service.RewardsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST endpoints for the Retailer Rewards Programme.
 *
 * Base path: /api/rewards
 */
@RestController
@RequestMapping("/api/rewards")
@CrossOrigin(origins = "*")   // allow requests from any origin (demo-friendly)
public class RewardsController {

    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    /**
     * GET /api/rewards
     *
     * Returns reward summaries (monthly + total) for ALL customers.
     */
    @GetMapping
    public ResponseEntity<List<CustomerRewardSummary>> getAllRewards() {
        return ResponseEntity.ok(rewardsService.getAllRewards());
    }

    /**
     * GET /api/rewards/{customerId}
     *
     * Returns the reward summary for a single customer.
     * Responds with 404 when the customer is not found.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getRewardsByCustomer(@PathVariable String customerId) {
        try {
            CustomerRewardSummary summary = rewardsService.getRewardsByCustomer(customerId);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * GET /api/rewards/calculate?amount={value}
     *
     * Utility endpoint: calculates reward points for any given purchase amount.
     * Useful for testing / front-end previews.
     */
    @GetMapping("/calculate")
    public ResponseEntity<?> calculatePoints(@RequestParam double amount) {
        if (amount < 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Amount must be non-negative."));
        }
        long points = rewardsService.calculatePoints(amount);
        return ResponseEntity.ok(Map.of(
                "amount", amount,
                "points", points
        ));
    }

    /**
     * GET /api/rewards/transactions
     *
     * Returns the full raw transaction dataset (useful for audit / debugging).
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(rewardsService.getAllTransactions());
    }
}
