package me.igorz.model;

import lombok.Data;

@Data
public class Statistics {

    // milliseconds
    private Integer time;

    private Double sum = 0.0;
    private Double max = Double.MIN_VALUE;
    private Double min = Double.MAX_VALUE;
    private Long count = 0L;

    public Statistics merge(Statistics src) {
        this.setCount(this.getCount() + src.getCount());
        this.setSum(this.getSum() + src.getSum());

        if (this.getMax().compareTo(src.getMax()) < 0) {
            this.setMax(src.getMax());
        }
        if (this.getMin().compareTo(src.getMin()) > 0) {
            this.setMin(src.getMin());
        }
        return this;
    }

    public Statistics update(Transaction transaction) {
        this.setCount(this.getCount() + 1);
        this.setSum(this.getSum() + transaction.getAmount());

        if (this.getMax().compareTo(transaction.getAmount()) < 0) {
            this.setMax(transaction.getAmount());
        }
        if (this.getMin().compareTo(transaction.getAmount()) > 0) {
            this.setMin(transaction.getAmount());
        }
        return this;
    }
}
