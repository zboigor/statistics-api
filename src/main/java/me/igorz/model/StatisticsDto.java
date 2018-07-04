package me.igorz.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StatisticsDto {

    private Double sum;
    private Double avg;
    private Double max;
    private Double min;
    private Long count;

    private StatisticsDto() {
    }

    public static StatisticsDto from(Statistics statistics) {
        StatisticsDto dto = new StatisticsDto();
        dto.sum = statistics.getSum();
        dto.max = statistics.getMax().compareTo(Double.MIN_VALUE) == 0 ? 0 : statistics.getMax();
        dto.min = statistics.getMin().compareTo(Double.MAX_VALUE) == 0 ? 0 : statistics.getMin();
        dto.count = statistics.getCount();

        dto.avg = statistics.getCount() > 0 ? statistics.getSum() / statistics.getCount() : 0.0;
        return dto;
    }
}
