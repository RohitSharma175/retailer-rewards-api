package com.retailer.rewards;

import com.retailer.rewards.model.CustomerRewardSummary;
import com.retailer.rewards.repository.TransactionRepository;
import com.retailer.rewards.service.RewardsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the rewards-point calculation logic and service layer.
 */
class RewardsServiceTest {

    private RewardsService service;

    @BeforeEach
    void setUp() {
        service = new RewardsService(new TransactionRepository());
    }

    // ── calculatePoints ───────────────────────────────────────────────────────

    @Test
    @DisplayName("$0 purchase earns 0 points")
    void zeroAmount() {
        assertEquals(0, service.calculatePoints(0));
    }

    @Test
    @DisplayName("$50 purchase earns 0 points (boundary)")
    void exactlyFiftyDollars() {
        assertEquals(0, service.calculatePoints(50));
    }

    @Test
    @DisplayName("$51 purchase earns 1 point")
    void justAboveFifty() {
        assertEquals(1, service.calculatePoints(51));
    }

    @Test
    @DisplayName("$75 purchase earns 25 points")
    void seventyFiveDollars() {
        assertEquals(25, service.calculatePoints(75));
    }

    @Test
    @DisplayName("$100 purchase earns 50 points (boundary)")
    void exactlyOneHundredDollars() {
        assertEquals(50, service.calculatePoints(100));
    }

    @Test
    @DisplayName("$101 purchase earns 52 points")
    void justAboveOneHundred() {
        assertEquals(52, service.calculatePoints(101));
    }

    @Test
    @DisplayName("$120 purchase earns 90 points (spec example)")
    void specExample() {
        // 2×20 + 1×50 = 90
        assertEquals(90, service.calculatePoints(120));
    }

    @Test
    @DisplayName("$200 purchase earns 250 points")
    void twoHundredDollars() {
        // 2×100 + 1×50 = 250
        assertEquals(250, service.calculatePoints(200));
    }

    @Test
    @DisplayName("$500 purchase earns 850 points")
    void fiveHundredDollars() {
        // 2×400 + 1×50 = 850
        assertEquals(850, service.calculatePoints(500));
    }

    @Test
    @DisplayName("Negative amount throws IllegalArgumentException")
    void negativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> service.calculatePoints(-10));
    }

    // ── getAllRewards ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("All 5 customers are returned")
    void allCustomersReturned() {
        List<CustomerRewardSummary> summaries = service.getAllRewards();
        assertEquals(5, summaries.size());
    }

    @Test
    @DisplayName("Each summary has monthly rewards and a positive total")
    void summariesHaveMonthlyData() {
        service.getAllRewards().forEach(s -> {
            assertFalse(s.getMonthlyRewards().isEmpty(),
                    s.getCustomerName() + " should have monthly rewards");
            assertTrue(s.getTotalPoints() >= 0,
                    s.getCustomerName() + " should have non-negative total");
        });
    }

    @Test
    @DisplayName("Vidit Sharma – total points match manual calculation")
    void aliceTotalPoints() {
        // T001 $120 → 90 | T002 $75 → 25 | T003 $200 → 250 | T004 $45 → 0
        // T005 $150 → 150 | T006 $110 → 70   total = 585
        CustomerRewardSummary alice = service.getRewardsByCustomer("C001");
        assertEquals(585, alice.getTotalPoints());
    }

    @Test
    @DisplayName("Rakhi Verma – March big-spender transaction ($500 → 850 pts)")
    void davidLeeMarchPoints() {
        CustomerRewardSummary david = service.getRewardsByCustomer("C004");
        long marchPts = david.getMonthlyRewards().stream()
                .filter(m -> m.getMonth().equals("MARCH"))
                .mapToLong(m -> m.getPoints())
                .sum();
        assertEquals(850, marchPts);
    }

    @Test
    @DisplayName("Unknown customer throws IllegalArgumentException")
    void unknownCustomer() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getRewardsByCustomer("UNKNOWN"));
    }
}
