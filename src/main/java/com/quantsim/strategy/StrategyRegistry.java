package com.quantsim.strategy;

import com.quantsim.domain.TradingStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Registry mapping strategy type names to strategy implementations.
 */
@Component
public class StrategyRegistry {

    public static final String MOVING_AVERAGE_CROSSOVER = "MOVING_AVERAGE_CROSSOVER";

    private final Map<String, TradingStrategy> strategies = Map.of(
            MOVING_AVERAGE_CROSSOVER, new MovingAverageCrossoverStrategy()
    );

    public TradingStrategy getStrategy(String strategyType) {
        TradingStrategy strategy = strategies.get(strategyType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown strategy type: " + strategyType);
        }
        return strategy;
    }
}
