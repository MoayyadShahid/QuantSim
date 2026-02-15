package com.quantsim.strategy;

import com.quantsim.domain.MarketData;
import com.quantsim.domain.TradeSignal;
import com.quantsim.domain.TradingStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Moving average crossover strategy.
 * BUY when short MA crosses above long MA; SELL when short MA crosses below long MA.
 * Requires parameters: shortWindow, longWindow (both positive integers, shortWindow < longWindow).
 */
public class MovingAverageCrossoverStrategy implements TradingStrategy {

    private static final String SHORT_WINDOW = "shortWindow";
    private static final String LONG_WINDOW = "longWindow";

    @Override
    public List<TradeSignal> generateSignals(List<MarketData> data, Map<String, Double> parameters) {
        if (data == null || data.isEmpty()) {
            return List.of();
        }

        int shortWindow = getRequiredParam(parameters, SHORT_WINDOW);
        int longWindow = getRequiredParam(parameters, LONG_WINDOW);

        if (shortWindow <= 0 || longWindow <= 0) {
            throw new IllegalArgumentException("shortWindow and longWindow must be positive");
        }
        if (shortWindow >= longWindow) {
            throw new IllegalArgumentException("shortWindow must be less than longWindow");
        }

        List<TradeSignal> signals = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (i < longWindow - 1) {
                signals.add(TradeSignal.HOLD);
                continue;
            }

            BigDecimal shortMa = computeSma(data, i - shortWindow + 1, i);
            BigDecimal longMa = computeSma(data, i - longWindow + 1, i);

            TradeSignal signal;
            if (i == longWindow - 1) {
                signal = TradeSignal.HOLD;
            } else {
                BigDecimal prevShortMa = computeSma(data, i - shortWindow, i - 1);
                BigDecimal prevLongMa = computeSma(data, i - longWindow, i - 1);

                boolean shortCrossedAbove = shortMa.compareTo(longMa) > 0 && prevShortMa.compareTo(prevLongMa) <= 0;
                boolean shortCrossedBelow = shortMa.compareTo(longMa) < 0 && prevShortMa.compareTo(prevLongMa) >= 0;

                if (shortCrossedAbove) {
                    signal = TradeSignal.BUY;
                } else if (shortCrossedBelow) {
                    signal = TradeSignal.SELL;
                } else {
                    signal = TradeSignal.HOLD;
                }
            }
            signals.add(signal);
        }
        return signals;
    }

    private int getRequiredParam(Map<String, Double> parameters, String key) {
        if (parameters == null || !parameters.containsKey(key)) {
            throw new IllegalArgumentException("Missing required parameter: " + key);
        }
        Double value = parameters.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Parameter '" + key + "' cannot be null");
        }
        return value.intValue();
    }

    private BigDecimal computeSma(List<MarketData> data, int from, int to) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = from; i <= to; i++) {
            sum = sum.add(data.get(i).getClose());
        }
        int count = to - from + 1;
        return sum.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
    }
}
