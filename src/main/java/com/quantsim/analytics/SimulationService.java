package com.quantsim.analytics;

import com.quantsim.domain.MarketData;
import com.quantsim.domain.TradeSignal;
import com.quantsim.domain.TradingStrategy;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Service that runs backtest simulations.
 * Iterates through market data, executes trades based on strategy signals,
 * and computes portfolio metrics (PnL, Sharpe ratio, max drawdown).
 */
@Service
public class SimulationService {

    private static final int SCALE = 4;
    private static final int TRADING_DAYS_PER_YEAR = 252;

    public BacktestResult runBacktest(TradingStrategy strategy, BigDecimal initialCapital,
                                     List<MarketData> data, Map<String, Double> parameters) {
        if (data == null || data.isEmpty()) {
            return new BacktestResult(
                    initialCapital, initialCapital, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO);
        }

        List<TradeSignal> signals = strategy.generateSignals(data, parameters);

        BigDecimal cash = initialCapital;
        BigDecimal position = BigDecimal.ZERO;
        BigDecimal peak = initialCapital;
        BigDecimal maxDrawdown = BigDecimal.ZERO;
        BigDecimal prevPortfolioValue = initialCapital;

        DescriptiveStatistics returnsStats = new DescriptiveStatistics();

        for (int i = 0; i < data.size(); i++) {
            MarketData md = data.get(i);
            TradeSignal signal = i < signals.size() ? signals.get(i) : TradeSignal.HOLD;
            BigDecimal close = md.getClose();

            switch (signal) {
                case BUY -> {
                    if (position.compareTo(BigDecimal.ZERO) == 0 && cash.compareTo(BigDecimal.ZERO) > 0) {
                        position = cash.divide(close, SCALE, RoundingMode.HALF_UP);
                        cash = BigDecimal.ZERO;
                    }
                }
                case SELL -> {
                    if (position.compareTo(BigDecimal.ZERO) > 0) {
                        cash = position.multiply(close);
                        position = BigDecimal.ZERO;
                    }
                }
                case HOLD -> { }
            }

            BigDecimal portfolioValue = cash.add(position.multiply(close));

            if (portfolioValue.compareTo(peak) > 0) {
                peak = portfolioValue;
            }
            if (peak.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal drawdown = peak.subtract(portfolioValue).divide(peak, SCALE, RoundingMode.HALF_UP);
                if (drawdown.compareTo(maxDrawdown) > 0) {
                    maxDrawdown = drawdown;
                }
            }

            if (prevPortfolioValue.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal dailyReturn = portfolioValue.subtract(prevPortfolioValue).divide(prevPortfolioValue, SCALE, RoundingMode.HALF_UP);
                returnsStats.addValue(dailyReturn.doubleValue());
            }
            prevPortfolioValue = portfolioValue;
        }

        BigDecimal finalCapital = cash.add(position.multiply(data.get(data.size() - 1).getClose()));
        BigDecimal totalPnL = finalCapital.subtract(initialCapital);
        BigDecimal sharpeRatio = computeSharpeRatio(returnsStats);

        return new BacktestResult(
                initialCapital.setScale(SCALE, RoundingMode.HALF_UP),
                finalCapital.setScale(SCALE, RoundingMode.HALF_UP),
                totalPnL.setScale(SCALE, RoundingMode.HALF_UP),
                sharpeRatio,
                maxDrawdown.setScale(SCALE, RoundingMode.HALF_UP));
    }

    private BigDecimal computeSharpeRatio(DescriptiveStatistics returnsStats) {
        if (returnsStats.getN() < 2) {
            return BigDecimal.ZERO;
        }
        double mean = returnsStats.getMean();
        double std = returnsStats.getStandardDeviation();
        if (std == 0) {
            return BigDecimal.ZERO;
        }
        double sharpe = (mean / std) * Math.sqrt(TRADING_DAYS_PER_YEAR);
        return BigDecimal.valueOf(sharpe).setScale(SCALE, RoundingMode.HALF_UP);
    }
}
