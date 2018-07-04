package me.igorz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @NotNull(message = "{validation.transaction.empty.amount}")
    private Double amount;

    @NotNull(message = "{validation.transaction.empty.timestamp}")
    private Long timestamp;
}
