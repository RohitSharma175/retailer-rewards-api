# Retailer Rewards API

A Spring Boot REST API that calculates customer reward points based on purchase history over a three-month period.

---

## Reward Rules

| Purchase Amount | Points Earned |
|----------------|---------------|
| ≤ $50 | 0 points |
| $50 – $100 | 1 point per dollar above $50 |
| > $100 | 1 pt/dollar in the $50–$100 band (**+50 pts**) + 2 pts/dollar above $100 |

**Example:** $120 purchase → 2 × $20 + 1 × $50 = **90 points**

---

## Tech Stack

- Java 17
- Spring Boot 3.2
- Maven
- JUnit 5 (unit tests)
- Lombok

---

## Getting Started

### Prerequisites
- JDK 17+
- Maven 3.8+

### Run the application

```bash
git clone https://github.com/RohitSharma175/retailer-rewards-api.git
cd retailer-rewards-api
mvn spring-boot:run
```

The API starts on **http://localhost:8080**.

### Run the tests

```bash
mvn test
```

---

## REST Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| `GET` | `/api/rewards` | Reward summaries for **all** customers |
| `GET` | `/api/rewards/{customerId}` | Reward summary for a **single** customer |
| `GET` | `/api/rewards/calculate?amount={n}` | Calculate points for any purchase amount |
| `GET` | `/api/rewards/transactions` | Full raw transaction dataset |

---

## Sample Dataset

Five customers, 26 transactions spread across January–March.

| Customer ID | Name | Jan | Feb | Mar | **Total** |
|-------------|------|-----|-----|-----|-----------|
| C001 | Vidit Sharma | 115 | 250 | 220 | **585** |
| C002 | Abhishek Kumar | 120 | 45 | 200 | **365** |
| C003 | Vishal Kumar | 450 | 380 | 180 | **1010** |
| C004 | Rakhi Verma | 5 | 92 | 850 | **947** |
| C005 | Varsha Singh | 35 | 105 | 140 | **280** |

---

## Example Responses

### GET /api/rewards/C001

```json
{
  "customerId": "C001",
  "customerName": "Vidit Sharma",
  "monthlyRewards": [
    { "year": 2025, "month": "JANUARY",  "monthNumber": 1, "points": 115 },
    { "year": 2025, "month": "FEBRUARY", "monthNumber": 2, "points": 250 },
    { "year": 2025, "month": "MARCH",    "monthNumber": 3, "points": 220 }
  ],
  "totalPoints": 585
}
```

### GET /api/rewards/calculate?amount=120

```json
{
  "amount": 120.0,
  "points": 90
}
```

---

## Project Structure

```
src/
├── main/java/com/retailer/rewards/
│   ├── RewardsApplication.java          # Entry point
│   ├── controller/RewardsController.java # REST layer
│   ├── service/RewardsService.java       # Business logic + point calc
│   ├── model/
│   │   ├── Transaction.java
│   │   ├── MonthlyReward.java
│   │   └── CustomerRewardSummary.java
│   └── repository/TransactionRepository.java  # In-memory dataset
└── test/java/com/retailer/rewards/
    └── RewardsServiceTest.java           # Unit tests
```

---

## Design Decisions

1. **In-memory data** – no database dependency; the `TransactionRepository` seeds 26 realistic transactions covering all edge cases (exactly $50, exactly $100, $0, $500).
2. **Pure function** – `RewardsService.calculatePoints(double)` is stateless and easily unit-tested.
3. **Stream-based aggregation** – transactions are grouped by customer and then by month using Java Streams, keeping the code concise and declarative.
4. **Boundary clarity** – the $50 and $100 thresholds are treated as exclusive lower bounds for each tier (amounts *strictly greater than* $50 earn tier-1 points; *strictly greater than* $100 earn tier-2 points).
