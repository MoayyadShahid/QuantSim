package com.quantsim.api;

import com.quantsim.analytics.BacktestResult;
import com.quantsim.analytics.SimulationService;
import com.quantsim.api.exception.TickerNotFoundException;
import com.quantsim.domain.MarketData;
import com.quantsim.domain.TradingStrategy;
import com.quantsim.repository.MarketDataRepository;
import com.quantsim.strategy.StrategyRegistry;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for backtest operations.
 * Fetches market data from the repository and runs simulations.
 */
@RestController
@RequestMapping("/api/v1")
public class BacktestController {

    private final MarketDataRepository marketDataRepository;
    private final StrategyRegistry strategyRegistry;
    private final SimulationService simulationService;

    public BacktestController(MarketDataRepository marketDataRepository,
                              StrategyRegistry strategyRegistry,
                              SimulationService simulationService) {
        this.marketDataRepository = marketDataRepository;
        this.strategyRegistry = strategyRegistry;
        this.simulationService = simulationService;
    }

    @PostMapping("/backtest")
    public ResponseEntity<BacktestResult> runBacktest(@RequestBody @Valid BacktestRequest request) {
        List<MarketData> data = marketDataRepository.findByTicker(request.getTicker())
                .orElseThrow(() -> new TickerNotFoundException(request.getTicker()));

        TradingStrategy strategy = strategyRegistry.getStrategy(request.getStrategyType());
        var parameters = request.getParameters() != null ? request.getParameters() : java.util.Map.<String, Double>of();

        BacktestResult result = simulationService.runBacktest(
                strategy,
                request.getInitialCapital(),
                data,
                parameters);

        return ResponseEntity.ok(result);
    }
}
