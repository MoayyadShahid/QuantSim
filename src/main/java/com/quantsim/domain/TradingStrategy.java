package com.quantsim.domain;

import java.util.List;
import java.util.Map;

/**
 * Interface for trading strategies that generate signals based on market data.
 */
public interface TradingStrategy {

    /**
     * Generates a list of trade signals, one per market data point, in chronological order.
     *
     * @param data       historical market data, sorted by date ascending
     * @param parameters strategy-specific parameters (e.g., shortWindow, longWindow)
     * @return list of signals aligned by index with the input data
     */
    List<TradeSignal> generateSignals(List<MarketData> data, Map<String, Double> parameters);
}
