package com.quantsim.repository;

import com.quantsim.domain.MarketData;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of {@link MarketDataRepository}.
 * Stores market data in a ConcurrentHashMap keyed by ticker symbol.
 * Thread-safe for concurrent reads and writes.
 */
@Repository
public class InMemoryMarketDataRepository implements MarketDataRepository {

    private final ConcurrentHashMap<String, List<MarketData>> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<List<MarketData>> findByTicker(String ticker) {
        if (ticker == null || ticker.isBlank()) {
            return Optional.empty();
        }
        List<MarketData> data = storage.get(ticker.toUpperCase());
        if (data == null || data.isEmpty()) {
            return Optional.empty();
        }
        List<MarketData> sorted = new ArrayList<>(data);
        sorted.sort(Comparator.comparing(MarketData::getDate));
        return Optional.of(Collections.unmodifiableList(sorted));
    }

    @Override
    public void save(String ticker, List<MarketData> data) {
        if (ticker == null || ticker.isBlank()) {
            throw new IllegalArgumentException("Ticker cannot be null or blank");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        storage.put(ticker.toUpperCase(), new ArrayList<>(data));
    }

    @Override
    public boolean existsByTicker(String ticker) {
        if (ticker == null || ticker.isBlank()) {
            return false;
        }
        List<MarketData> data = storage.get(ticker.toUpperCase());
        return data != null && !data.isEmpty();
    }
}
