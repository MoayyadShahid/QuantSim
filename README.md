# QuantSim

A quantitative backtesting engine built with Java 17+, Spring Boot 3, and Domain-Driven Design principles.

## Tech Stack

- **Java 17+**
- **Spring Boot 3** (Web, Data JPA, Validation)
- **PostgreSQL** (driver included; DB not yet configured)
- **Apache Commons Math** (statistical calculations)
- **Maven**

## Features

- **Domain Layer**: `MarketData` entity, `TradeSignal` enum, `TradingStrategy` interface
- **Strategy**: Moving Average Crossover (configurable short/long windows)
- **Analytics**: Backtest simulation with PnL, Sharpe ratio, and max drawdown
- **API**: REST endpoint for running backtests
- **Data**: CSV-based market data loaded at startup from `historical_data.csv`

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven (or use the included `./mvnw` wrapper)

### Build & Run

```bash
./mvnw spring-boot:run
```

Or with Maven installed:

```bash
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

### Run a Backtest

```bash
curl -X POST http://localhost:8080/api/v1/backtest \
  -H "Content-Type: application/json" \
  -d '{
    "ticker": "SPY",
    "strategyType": "MOVING_AVERAGE_CROSSOVER",
    "initialCapital": 100000,
    "parameters": {
      "shortWindow": 5,
      "longWindow": 15
    }
  }'
```

### Response

```json
{
  "initialCapital": 100000,
  "finalCapital": 222418.22,
  "totalPnL": 122418.22,
  "sharpeRatio": 1.16,
  "maxDrawdown": 0.15
}
```

## API Reference

| Endpoint | Method | Description |
|---------|--------|-------------|
| `/api/v1/backtest` | POST | Run a backtest with the specified strategy and parameters |

### Request Body

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `ticker` | string | Yes | Stock ticker (e.g., "SPY"). Must exist in loaded data. |
| `strategyType` | string | Yes | `MOVING_AVERAGE_CROSSOVER` |
| `initialCapital` | number | Yes | Starting capital (positive) |
| `parameters` | object | No | Strategy-specific params. For MA crossover: `shortWindow`, `longWindow` |

### Strategies

- **MOVING_AVERAGE_CROSSOVER**: BUY when short MA crosses above long MA, SELL when it crosses below. Parameters: `shortWindow` (int), `longWindow` (int).

## Market Data

Historical data is loaded from `src/main/resources/historical_data.csv` at startup. The CSV format:

```
Date,Open,High,Low,Close,Volume
2024-01-02,475.23,478.45,474.12,477.89,85000000
...
```

To regenerate the CSV using Python (yfinance):

```bash
cd src/main/resources
python historical_data.py
```

Move the generated `historical_data.csv` into `src/main/resources/` if the script outputs it elsewhere.

## Project Structure

```
src/main/java/com/quantsim/
├── QuantSimApplication.java
├── domain/           # MarketData, TradeSignal, TradingStrategy
├── repository/       # MarketDataRepository, InMemoryMarketDataRepository
├── service/          # CsvDataLoaderService
├── strategy/         # MovingAverageCrossoverStrategy, StrategyRegistry
├── analytics/        # BacktestResult, SimulationService
└── api/              # BacktestController, BacktestRequest, exception handlers
```

## License

MIT
