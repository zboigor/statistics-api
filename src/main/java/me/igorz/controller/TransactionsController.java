package me.igorz.controller;

import lombok.extern.slf4j.Slf4j;
import me.igorz.model.Transaction;
import me.igorz.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/transactions")
public class TransactionsController {

    private final StatisticsService statisticsService;

    public TransactionsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping
    public ResponseEntity addTransaction(@RequestBody @Valid Transaction transaction) {
        return statisticsService.validateTransaction(transaction)
                .map(tr -> {
                    statisticsService.addTransaction(transaction);
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                })
                .orElse(ResponseEntity.noContent().build());
    }
}
