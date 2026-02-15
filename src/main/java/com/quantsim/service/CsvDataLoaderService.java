package com.quantsim.service;

import com.quantsim.domain.MarketData;
import com.quantsim.repository.MarketDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service that loads historical market data from a CSV file at startup.
 * Reads {@code historical_data.csv} from classpath (src/main/resources).
 * CSV format: Date,Open,High,Low,Close,Volume
 * Assumes ticker is "SPY" for the loaded data.
 */
@Service
public class CsvDataLoaderService {

    private static final Logger log = LoggerFactory.getLogger(CsvDataLoaderService.class);
    private static final String CSV_FILE = "historical_data.csv";
    private static final String TICKER = "SPY";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final MarketDataRepository marketDataRepository;

    public CsvDataLoaderService(MarketDataRepository marketDataRepository) {
        this.marketDataRepository = marketDataRepository;
    }

    @PostConstruct
    public void loadHistoricalData() {
        try {
            var resource = new ClassPathResource(CSV_FILE);
            if (!resource.exists()) {
                log.warn("CSV file '{}' not found in classpath. Skipping data load.", CSV_FILE);
                return;
            }

            List<MarketData> data = parseCsv(resource);
            if (!data.isEmpty()) {
                marketDataRepository.save(TICKER, data);
                log.info("Loaded {} market data records for ticker '{}' from {}", data.size(), TICKER, CSV_FILE);
            } else {
                log.warn("No valid data rows found in {}", CSV_FILE);
            }
        } catch (IOException e) {
            log.error("Failed to load CSV file '{}': {}", CSV_FILE, e.getMessage());
        }
    }

    private List<MarketData> parseCsv(org.springframework.core.io.Resource resource) throws IOException {
        List<MarketData> result = new ArrayList<>();

        try (var reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null || !headerLine.trim().toLowerCase().startsWith("date")) {
                log.warn("CSV header missing or invalid. Expected: Date,Open,High,Low,Close,Volume");
                return result;
            }

            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    MarketData md = parseRow(line);
                    if (md != null) {
                        result.add(md);
                    }
                } catch (Exception e) {
                    log.debug("Skipping invalid row {}: {} - {}", lineNum, line, e.getMessage());
                }
            }
        }

        return result;
    }

    private MarketData parseRow(String line) {
        String[] parts = line.split(",");
        if (parts.length < 6) {
            return null;
        }

        LocalDate date = LocalDate.parse(parts[0].trim(), DATE_FORMATTER);
        BigDecimal open = new BigDecimal(parts[1].trim());
        BigDecimal high = new BigDecimal(parts[2].trim());
        BigDecimal low = new BigDecimal(parts[3].trim());
        BigDecimal close = new BigDecimal(parts[4].trim());
        BigDecimal volume = new BigDecimal(parts[5].trim());

        return new MarketData(date, TICKER, open, high, low, close, volume);
    }
}
