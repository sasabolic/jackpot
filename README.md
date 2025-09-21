# Jackpot Service

A Spring Boot (Java 21) service that simulates a jackpot system. It records incoming bets, contributes a portion of them to a jackpot pool, and evaluates rewards using configurable contribution/reward models.

---

## 🧠 The Core Logic

The Jackpot Service is designed to be highly flexible. Its core behavior, including how contributions are calculated and how rewards are evaluated, is defined by configurable models. This allows you to change the jackpot's rules without deploying new code.

### **Contribution Models**

This model determines how a portion of a bet's stake is added to the jackpot pool.

* **Fixed:** A set percentage of every bet's stake is contributed to the jackpot. For example, a 5.00% rate means a €10 bet always adds €0.50 to the jackpot. This provides a predictable and consistent growth rate for the pool.

* **Variable:** The contribution rate changes dynamically based on the current jackpot pool size. This is often used to implement a "decay factor" where a smaller percentage is contributed as the jackpot grows. This can help manage the overall rate of growth and prevent the jackpot from becoming too large too quickly.

### **Reward Models**

This model evaluates whether a bet wins a reward and calculates the chance of winning.

* **Fixed Chance:** The probability of winning is a static percentage, regardless of the jackpot's current size. For example, a 0.01% chance means every bet has the same fixed odds of winning.

* **Variable Chance:** The probability of winning increases as the jackpot pool grows. This model uses **linear interpolation** to calculate the chance between a minimum and maximum pool size. For instance, the chance might start at 2.5% when the pool is at its minimum and increase to 100% when it reaches a predefined maximum, encouraging more bets as the jackpot gets bigger.

---

## 🧱 Tech Stack

- **Java** 21, **Spring Boot** 3.5.x
- Spring Kafka for asynchronous messaging, event-driven communication
- H2 Database for in‑memory persistence
- Lombok for boilerplate code reduction
- JUnit 5, Mockito, AssertJ for comprehensive testing
- Gradle for building and dependency management

---

## 📦 Project Layout (Hexagonal)

```
jackpot/
├─ src/main/java/com/example/jackpot
│  ├─ domain/                       # Entities (Bet, Jackpot, Money, Percentage, …)
│  │  ├─ contribution/              # Contribution calculators
│  │  └─ reward/                    # Reward evaluators
│  ├─ application/                  # Use cases (services) & ports
│  │  ├─ port/in/                   # Inbound ports (PlaceBetService, BetConsumer, ...)
│  │  └─ port/out/                  # Outbound ports (Repositories, BetProducer, ...)
│  ├─ adapter
│  │  ├─ in/
│  │  │  ├─ rest/                   # REST controller & DTOs
│  │  │  └─ messaging/kafka/        # Kafka consumer
│  │  └─ out/
│  │     ├─ messaging/kafka/        # Kafka producer & message DTO
│  │     └─ persistence/jpa/        # JPA entities, mappers, repositories
│  └─ JackpotApplication.java
├─ src/main/resources/
│  ├─ application.yml               # H2 + Kafka config
│  └─ data.sql                      # Sample jackpots
├─ docker-compose.yml               # Local Kafka (KRaft) single node
└─ build.gradle
```

---

## ▶️ Quickstart

### Prerequisites
- JDK 21+
- Docker (for Kafka)

### 1) Start Kafka (docker compose)
From the project's root folder, start the Kafka broker using Docker:
```bash
docker compose up -d
```
The broker will be available at **localhost:49092**.

### 2) Build & run
Run the Spring Boot application from the same folder:
```bash
./gradlew clean bootRun
```
The app starts on **http://localhost:8080**. It uses an **in‑memory H2** database, which is pre-populated with sample data.

> ⚠️ **Note:** The in-memory database is configured with `create-drop` and will be **deleted and recreated** every time the application starts. All data is temporary and will be lost on shutdown.
---

## 🔎 View the Data (H2 Console)

You can inspect the database and the sample data directly in your browser using the **H2 console**.

1.  If not already started, start the application by running `./gradlew bootRun` from the project's root folder.
2.  Open your browser and navigate to:
    **`http://localhost:8080/h2-console`**
3.  Use the following credentials to connect:
  * **JDBC URL:** `jdbc:h2:mem:jackpot;DB_CLOSE_DELAY=-1`
  * **Username:** `sa`
  * **Password:** (leave blank)

You'll be able to view tables such as `JACKPOT` and `JACKPOT_REWARD` and run SQL queries to explore the sample data loaded from `data.sql`.

---

## 🧪 Sample Data

The `src/main/resources/data.sql` file loads four distinct jackpots at startup. Each one demonstrates a different combination of contribution and reward models:

| Jackpot ID | Contribution Model | Reward Model |
| :--- | :--- | :--- |
| `11111111-1111-1111-1111-111111111111` | Fixed | Fixed Chance |
| `22222222-2222-2222-2222-222222222222` | Variable | Fixed Chance |
| `33333333-3333-3333-3333-333333333333` | Fixed | Variable Chance |
| `44444444-4444-4444-4444-444444444444` | Variable | Variable Chance |

The configuration for each model is stored as a JSON payload in the database.

* **Fixed Contribution Example:**
    ```json
    { "type": "FIXED", "schemaVersion": 1, "config": { "rate": "5.00" } }
    ```
* **Variable Chance Reward Example:**
    ```json
    { "type": "VARIABLE_CHANCE", "schemaVersion": 1,
      "config": {
        "startPercent": "2.50",
        "minPool": { "amount": "100.00", "currency": "EUR" },
        "maxPool": { "amount": "1500.00", "currency": "EUR" }
      }
    }
    ```

---

## 🌐 REST API

### 1) Place a Bet
This endpoint accepts a bet, publishes it to a Kafka topic, and returns immediately with a `202 Accepted` status.

`POST /api/bets`

Request body example:
```json
{
  "betId":     "a0d1c7ae-98d1-4f7f-bfd0-5f2c1e1a9b11",
  "userId":    "b4019a2c-d1a2-4d18-ae8d-3e6a734f60a0",
  "jackpotId": "11111111-1111-1111-1111-111111111111",
  "betAmount": { 
    "amount": "10.00", 
    "currency": "EUR"
  }
}
```

Validation:
- `betId`, `userId`, `jackpotId`: non‑blank, UUID
- `betAmount.amount`: positive string number with **up to 2 decimals** (custom `@ValidAmount`)
- `betAmount.currency`: `^[A-Z]{3}$` (ISO code)

### 2) Check for a Reward
Check the outcome of a bet after it has been processed.

`GET /api/bets/{betId}/reward`

Response (no win):
```json
{ "won": false, "reward": null }
```

Response (win example):
```json
{ "won": true, 
  "reward": { 
    "amount": "1234.00", 
    "currency": "EUR"
  }
}
```

---

## 🧪 Testing

Run all tests:
```bash
./gradlew test
```

Highlights:
- **Controllers**: MVC tests for request/response & validation
- **Domain**: calculators/evaluators (including parameterized probability tests)
- **Persistence**: JPA slice tests with H2; unique constraints & FK checks
- **Kafka**: `@EmbeddedKafka` for producer/consumer integration
- **Logging**: error path tests with LogCaptor

---

## 🧩 Domain Overview

- `Money`, `Percentage`, `CycleNumber`, `JackpotCycle` immutable value objects
- `Jackpot` aggregate (state: `currentCycle`, `currentPool`, etc.)
- `ContributionCalculator`:
  - `FixedContributionCalculator`
  - `VariableContributionCalculator` (with decay factor)
- `RewardEvaluator`:
  - `FixedChanceRewardEvaluator`
  - `VariableChanceRewardEvaluator` (linear interpolation between min/max chance over min/max pool)

All models validate invariants (positive amounts, same currencies, ranges, etc.).

---

## 🔌 Ports & Adapters

- **Inbound ports** (`application.port.in`): `PlaceBetService`, `BetProcessingService`, `BetConsumer`, `RewardQueryService`
- **Outbound ports** (`application.port.out`): `BetRepository`, `JackpotRepository`, `JackpotContributionRepository`, `JackpotRewardRepository`, `BetProducer`

- **Inbound adapters**:
  - REST: `BetController`
  - Kafka: `KafkaBetConsumer`

- **Outbound adapters**:
  - Kafka: `KafkaBetProducer`
  - JPA: `*RepositoryAdapter` + `*JpaRepository` + `*Entity` + mappers

---

## 📫 End‑to‑end demo (quick)

```bash
# Start Kafka and the app
docker compose up -d
./gradlew bootRun

# Place a bet
curl -i -X POST http://localhost:8080/api/bets   -H 'Content-Type: application/json'   -d '{
        "betId":"00000000-0000-0000-0000-000000000001",
        "userId":"00000000-0000-0000-0000-000000000002",
        "jackpotId":"11111111-1111-1111-1111-111111111111",
        "betAmount":{"amount":"10.00","currency":"EUR"}
      }'

# Later, check reward
curl -s http://localhost:8080/api/bets/00000000-0000-0000-0000-000000000001/reward | jq .
```
