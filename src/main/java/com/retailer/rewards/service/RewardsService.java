package com.retailer.rewards.service;

import com.retailer.rewards.model.CustomerRewardSummary;
import com.retailer.rewards.model.MonthlyReward;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Core business logic for the rewards programme.
 *
 * Point rules
 * ───────────
 *  • 2 points for every dollar spent ABOVE $100 in a single transaction
 *  • 1 point for every dollar spent BETWEEN $50 and $100 (exclusive) in a single transaction
 *  • Amounts at or below $50 earn 0 points
 *
 * Example: $120 purchase → 2 × 20 + 1 × 50 = 90 points
 */
@Service
public class RewardsService {

    private final TransactionRepository transactionRepository;

    public RewardsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns reward summaries for every customer in the dataset.
     */
    public List<CustomerRewardSummary> getAllRewards() {
        List<Transaction> all = transactionRepository.findAll();
        return buildSummaries(all);
    }

    /**
     * Returns the reward summary for a single customer.
     *
     * @param customerId case-insensitive customer identifier
     * @throws IllegalArgumentException when the customer is not found
     */
    public CustomerRewardSummary getRewardsByCustomer(String customerId) {
        List<Transaction> customerTxns = transactionRepository.findByCustomerId(customerId);

        if (customerTxns.isEmpty()) {
            throw new IllegalArgumentException(
                    "No transactions found for customer: " + customerId);
        }

        return buildSummaries(customerTxns).get(0);
    }

    /**
     * Returns all raw transactions (useful for transparency / debugging).
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // ── Core calculation ──────────────────────────────────────────────────────

    /**
     * Calculates reward points for a single transaction amount.
     *
     * <pre>
     *  amount ≤ 50          → 0 pts
     *  50 < amount ≤ 100    → 1 pt per dollar above $50
     *  amount > 100         → 1 pt per dollar in ($50–$100] range (= 50 pts)
     *                          + 2 pts per dollar above $100
     * </pre>
     *
     * @param amount transaction amount (must be ≥ 0)
     * @return reward points earned (always ≥ 0)
     */
    public long calculatePoints(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Transaction amount cannot be negative.");
        }

        long points = 0;

        if (amount > 100) {
            // Tier 2: every dollar above $100 earns 2 points
            points += 2L * (long) (amount - 100);
            // Tier 1: the $50–$100 band always contributes 50 points when amount > $100
            points += 50;
        } else if (amount > 50) {
            // Tier 1 only: every dollar above $50 (up to $100) earns 1 point
            points += (long) (amount - 50);
        }
        // amount ≤ 50 → 0 points (no action required)

        return points;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private List<CustomerRewardSummary> buildSummaries(List<Transaction> transactions) {
        // Group by customerId
        Map<String, List<Transaction>> byCustomer = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCustomerId));

        return byCustomer.entrySet().stream()
                .map(entry -> buildCustomerSummary(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CustomerRewardSummary::getCustomerId))
                .toList();
    }

    private CustomerRewardSummary buildCustomerSummary(String customerId,
                                                        List<Transaction> txns) {
        String customerName = txns.get(0).getCustomerName();

        // Group transactions by "YYYY-MM" key, then map to MonthlyReward
        Map<String, List<Transaction>> byMonth = txns.stream()
                .collect(Collectors.groupingBy(t ->
                        t.getTransactionDate().getYear() + "-" +
                        String.format("%02d", t.getTransactionDate().getMonthValue())));

        List<MonthlyReward> monthlyRewards = byMonth.entrySet().stream()
                .map(entry -> {
                    List<Transaction> monthTxns = entry.getValue();
                    // Safe: at least one txn guaranteed
                    var sample = monthTxns.get(0).getTransactionDate();
                    long pts = monthTxns.stream()
                            .mapToLong(t -> calculatePoints(t.getAmount()))
                            .sum();

                    return MonthlyReward.builder()
                            .year(sample.getYear())
                            .month(sample.getMonth().name())       // e.g. "JANUARY"
                            .monthNumber(sample.getMonthValue())   // 1-12
                            .points(pts)
                            .build();
                })
                .sorted(Comparator.comparingInt(MonthlyReward::getMonthNumber))
                .toList();

        long total = monthlyRewards.stream().mapToLong(MonthlyReward::getPoints).sum();

        return CustomerRewardSummary.builder()
                .customerId(customerId)
                .customerName(customerName)
                .monthlyRewards(monthlyRewards)
                .totalPoints(total)
                .build();
    }
}
