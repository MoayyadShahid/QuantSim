package com.quantsim.repository;

import com.quantsim.domain.MarketData;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing market data by ticker symbol.
 * Supports both in-memory and future JPA-backed implementations.
 */
public interface MarketDataRepository {

    /**
     * Finds all market data for the given ticker symbol, sorted by date ascending.
     *
     * @param ticker the stock ticker symbol (e.g., "SPY", "AAPL")
     * @return optional list of market data; empty if ticker not found
     */
    Optional<List<MarketData>> findByTicker(String ticker);

    /**
     * Saves market data for a ticker. Replaces any existing data for that ticker.
     *
     * @param ticker the stock ticker symbol
     * @param data   list of market data points, typically sorted by date
     */
    void save(String ticker, List<MarketData> data);

    /**
     * Checks whether data exists for the given ticker.
     *
     * @param ticker the stock ticker symbol
     * @return true if data exists for the ticker
     */
    boolean existsByTicker(String ticker);
}
