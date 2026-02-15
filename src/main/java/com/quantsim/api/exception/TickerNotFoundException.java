package com.quantsim.api.exception;

/**
 * Thrown when market data for a requested ticker symbol is not found in the repository.
 */
public class TickerNotFoundException extends RuntimeException {

    public TickerNotFoundException(String ticker) {
        super("Market data not found for ticker: " + ticker);
    }
}
