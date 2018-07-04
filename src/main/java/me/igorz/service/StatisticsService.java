package me.igorz.service;

import lombok.extern.slf4j.Slf4j;
import me.igorz.configuration.StatisticsProperties;
import me.igorz.model.Statistics;
import me.igorz.model.Transaction;
import me.igorz.util.Pair;
import me.igorz.util.StatisticsSet;
import me.igorz.util.Utils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatisticsService {

    private final StatisticsProperties statisticsProperties;

    // store aggregated by seconds statistics
    private Map<Long, Pair<Statistics, StatisticsSet>> statisticsMap;

    public StatisticsService(StatisticsProperties statisticsProperties) {
        this.statisticsProperties = statisticsProperties;
        this.statisticsMap = new ConcurrentHashMap<>();
    }

    public Optional<Transaction> validateTransaction(Transaction transaction) {
        Instant now = Instant.now();
        if (now.minus(statisticsProperties.getTime(), ChronoUnit.SECONDS).toEpochMilli() > transaction.getTimestamp()) {
            log.info("Transaction will be ignored: {}", transaction);
            return Optional.empty();
        } else {
            return Optional.of(transaction);
        }
    }

    @Async
    public void addTransaction(Transaction transaction) {
        log.info("Computing statistics for transaction: {}", transaction);
        statisticsMap.compute(Utils.dropMilliseconds(transaction.getTimestamp()), (aLong, statistics) -> {
            if (statistics == null) {
                statistics = new Pair<>(new Statistics(), new StatisticsSet());
            }
            statistics.getFirst().update(transaction);
            statistics.getSecond().addTransaction(transaction);
            return statistics;
        });
    }

    /**
     * Get actual statistics.
     * <p>
     * Merges statistics from last 60 seconds + a part of last second
     * Operates in O(60) + O(log(1000)) which is actually O(1)
     */
    public Statistics getStatistics() {
        Instant now = Instant.now();
        log.info("Loading statistics for {}", now.toString());
        Long fromTime = Utils.dropMilliseconds(now.minus(statisticsProperties.getTime(), ChronoUnit.SECONDS).toEpochMilli());
        Statistics statistics = statisticsMap.entrySet()
                .stream()
                .filter(longStatisticsEntry -> fromTime < longStatisticsEntry.getKey())
                .map(longPairEntry -> longPairEntry.getValue().getFirst())
                .reduce(new Statistics(), Statistics::merge);

        Pair<Statistics, StatisticsSet> lastSecondStatistics = statisticsMap.get(fromTime);
        if (lastSecondStatistics != null) {
            statistics.merge(lastSecondStatistics.getSecond().getStatistics(now.toEpochMilli()));
        }
        return statistics;
    }

    // clean old statistics every 10 seconds
    @Scheduled(fixedRate = 10 * 1000)
    public void cleanOldStatistics() {
        Instant now = Instant.now();
        Long fromTime = Utils.dropMilliseconds(now.minus(statisticsProperties.getTime() + 1, ChronoUnit.SECONDS).toEpochMilli());

        Set<Long> toRemove = statisticsMap.entrySet()
                .stream()
                .filter(longStatisticsEntry -> fromTime >= longStatisticsEntry.getKey())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        toRemove.forEach(aLong -> statisticsMap.remove(aLong));
    }
}
