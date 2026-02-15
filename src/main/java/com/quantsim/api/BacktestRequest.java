package com.quantsim.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Request DTO for the backtest API.
 * Contains ticker, strategy type, initial capital, and strategy parameters.
 */
public class BacktestRequest {

    @NotBlank(message = "Ticker is required")
    private String ticker;

    @NotBlank(message = "Strategy type is required")
    private String strategyType;

    @NotNull(message = "Initial capital is required")
    @DecimalMin(value = "0.01", message = "Initial capital must be positive")
    private BigDecimal initialCapital;

    private Map<String, Double> parameters;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public BigDecimal getInitialCapital() {
        return initialCapital;
    }

    public void setInitialCapital(BigDecimal initialCapital) {
        this.initialCapital = initialCapital;
    }

    public Map<String, Double> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Double> parameters) {
        this.parameters = parameters;
    }
}
