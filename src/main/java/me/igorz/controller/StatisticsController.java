package me.igorz.controller;

import lombok.extern.slf4j.Slf4j;
import me.igorz.model.StatisticsDto;
import me.igorz.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<StatisticsDto> getStatistics() {
        return ResponseEntity.ok(StatisticsDto.from(statisticsService.getStatistics()));
    }
}
