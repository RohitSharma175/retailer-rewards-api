package com.retailer.rewards.repository;

import com.retailer.rewards.model.Transaction;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * In-memory store that seeds a realistic 3-month transaction dataset.
 *
 * Customers
 * ---------
 * C001  Vidit Sharma
 * C002  Abhishek Kumar
 * C003  Vishal Kumar
 * C004  Rakhi Verma
 * C005  Varsha Singh
 *
 * Period: January – March (current year)
 */
@Repository
public class TransactionRepository {

    private final List<Transaction> transactions;

    public TransactionRepository() {
        int year = LocalDate.now().getYear();

        transactions = List.of(

            /* ── Vidit Sharma (C001) ──────────────────────────────────── */
            // Jan: $120 → 2×20 + 1×50 = 90 pts
            build("T001", "C001", "Vidit Sharma",  LocalDate.of(year, 1,  5),  120.00),
            // Jan: $75  → 1×25 = 25 pts
            build("T002", "C001", "Vidit Sharma",  LocalDate.of(year, 1, 18),   75.00),
            // Feb: $200 → 2×100 + 1×50 = 250 pts
            build("T003", "C001", "Vidit Sharma",  LocalDate.of(year, 2,  3),  200.00),
            // Feb: $45  → 0 pts
            build("T004", "C001", "Vidit Sharma",  LocalDate.of(year, 2, 22),   45.00),
            // Mar: $150 → 2×50 + 1×50 = 150 pts
            build("T005", "C001", "Vidit Sharma",  LocalDate.of(year, 3, 10),  150.00),
            // Mar: $110 → 2×10 + 1×50 = 70 pts
            build("T006", "C001", "Vidit Sharma",  LocalDate.of(year, 3, 28),  110.00),

            /* ── Abhishek Kumar (C002) ───────────────────────────────────── */
            // Jan: $60  → 1×10 = 10 pts
            build("T007", "C002", "Abhishek Kumar",   LocalDate.of(year, 1,  8),   60.00),
            // Jan: $130 → 2×30 + 1×50 = 110 pts
            build("T008", "C002", "Abhishek Kumar",   LocalDate.of(year, 1, 20),  130.00),
            // Feb: $95  → 1×45 = 45 pts
            build("T009", "C002", "Abhishek Kumar",   LocalDate.of(year, 2, 14),   95.00),
            // Mar: $175 → 2×75 + 1×50 = 200 pts
            build("T010", "C002", "Abhishek Kumar",   LocalDate.of(year, 3,  7),  175.00),
            // Mar: $50  → 0 pts  (boundary: exactly $50, range is >$50)
            build("T011", "C002", "Abhishek Kumar",   LocalDate.of(year, 3, 25),   50.00),

            /* ── Vishal Kumar (C003) ────────────────────────────────────── */
            // Jan: $300 → 2×200 + 1×50 = 450 pts
            build("T012", "C003", "Vishal Kumar",    LocalDate.of(year, 1,  3),  300.00),
            // Feb: $250 → 2×150 + 1×50 = 350 pts
            build("T013", "C003", "Vishal Kumar",    LocalDate.of(year, 2, 17),  250.00),
            // Feb: $80  → 1×30 = 30 pts
            build("T014", "C003", "Vishal Kumar",    LocalDate.of(year, 2, 28),   80.00),
            // Mar: $100 → 0 pts  (boundary: exactly $100 earns tier-1 but not tier-2)
            build("T015", "C003", "Vishal Kumar",    LocalDate.of(year, 3, 12),  100.00),
            // Mar: $165 → 2×65 + 1×50 = 180 pts
            build("T016", "C003", "Vishal Kumar",    LocalDate.of(year, 3, 22),  165.00),

            /* ── Rakhi Verma (C004) ──────────────────────────────────────── */
            // Jan: $40  → 0 pts
            build("T017", "C004", "Rakhi Verma",      LocalDate.of(year, 1, 11),   40.00),
            // Jan: $55  → 1×5 = 5 pts
            build("T018", "C004", "Rakhi Verma",      LocalDate.of(year, 1, 29),   55.00),
            // Feb: $101 → 2×1 + 1×50 = 52 pts
            build("T019", "C004", "Rakhi Verma",      LocalDate.of(year, 2,  6),  101.00),
            // Feb: $90  → 1×40 = 40 pts
            build("T020", "C004", "Rakhi Verma",      LocalDate.of(year, 2, 19),   90.00),
            // Mar: $500 → 2×400 + 1×50 = 850 pts  (big-spender transaction)
            build("T021", "C004", "Rakhi Verma",      LocalDate.of(year, 3,  1),  500.00),

            /* ── Varsha Singh (C005) ──────────────────────────────────────── */
            // Jan: $85  → 1×35 = 35 pts
            build("T022", "C005", "Varsha Singh",      LocalDate.of(year, 1, 15),   85.00),
            // Feb: $120 → 2×20 + 1×50 = 90 pts
            build("T023", "C005", "Varsha Singh",      LocalDate.of(year, 2,  9),  120.00),
            // Feb: $65  → 1×15 = 15 pts
            build("T024", "C005", "Varsha Singh",      LocalDate.of(year, 2, 23),   65.00),
            // Mar: $30  → 0 pts
            build("T025", "C005", "Varsha Singh",      LocalDate.of(year, 3, 16),   30.00),
            // Mar: $145 → 2×45 + 1×50 = 140 pts
            build("T026", "C005", "Varsha Singh",      LocalDate.of(year, 3, 30),  145.00)
        );
    }

    /** Returns all transactions. */
    public List<Transaction> findAll() {
        return transactions;
    }

    /** Returns transactions for a specific customer. */
    public List<Transaction> findByCustomerId(String customerId) {
        return transactions.stream()
                .filter(t -> t.getCustomerId().equalsIgnoreCase(customerId))
                .toList();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Transaction build(String id, String custId, String name,
                              LocalDate date, double amount) {
        return Transaction.builder()
                .transactionId(id)
                .customerId(custId)
                .customerName(name)
                .transactionDate(date)
                .amount(amount)
                .build();
    }
}
