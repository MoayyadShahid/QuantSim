package com.quantsim.analytics;

import java.math.BigDecimal;

/**
 * Immutable result of a backtest simulation.
 */
public record BacktestResult(
        BigDecimal initialCapital,
        BigDecimal finalCapital,
        BigDecimal totalPnL,
        BigDecimal sharpeRatio,
        BigDecimal maxDrawdown
) {}
