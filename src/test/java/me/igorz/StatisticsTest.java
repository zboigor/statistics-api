package me.igorz;

import me.igorz.model.Statistics;
import me.igorz.model.StatisticsDto;
import me.igorz.model.Transaction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatisticsTest {

    @Test
    public void testEmptyDto() {
        Statistics statistics1 = new Statistics();
        StatisticsDto statisticsDto = StatisticsDto.from(statistics1);

        assertEquals(0, statisticsDto.getSum(), 0);
        assertEquals(0, statisticsDto.getCount(), 0);
        assertEquals(0, statisticsDto.getMax(), 0);
        assertEquals(0, statisticsDto.getMin(), 0);
        assertEquals(0, statisticsDto.getAvg(), 0);
    }

    @Test
    public void testStatisticsDto() {
        Statistics statistics1 = new Statistics();
        statistics1.setSum(6.0);
        statistics1.setCount(2L);
        statistics1.setMax(4.0);
        statistics1.setMin(2.0);

        StatisticsDto statisticsDto = StatisticsDto.from(statistics1);

        assertEquals(6, statisticsDto.getSum(), 0);
        assertEquals(2, statisticsDto.getCount(), 0);
        assertEquals(4, statisticsDto.getMax(), 0);
        assertEquals(2, statisticsDto.getMin(), 0);
        assertEquals(3, statisticsDto.getAvg(), 0);
    }

    @Test
    public void testEmptyMerge() {
        Statistics statistics1 = new Statistics();
        Statistics statistics2 = new Statistics();
        statistics2.setSum(1.0);
        statistics2.setCount(1L);
        statistics2.setMax(3.0);
        statistics2.setMin(3.0);

        statistics1.merge(statistics2);

        assertEquals(1.0, statistics1.getSum(), 0);
        assertEquals(1L, statistics1.getCount(), 0);
        assertEquals(3.0, statistics1.getMax(), 0);
        assertEquals(3.0, statistics1.getMin(), 0);
    }

    @Test
    public void testMerge() {
        Statistics statistics1 = new Statistics();
        statistics1.setSum(1.0);
        statistics1.setCount(1L);
        statistics1.setMax(2.0);
        statistics1.setMin(2.0);

        Statistics statistics2 = new Statistics();
        statistics2.setSum(1.0);
        statistics2.setCount(1L);
        statistics2.setMax(3.0);
        statistics2.setMin(3.0);

        statistics1.merge(statistics2);

        assertEquals(2.0, statistics1.getSum(), 0);
        assertEquals(2L, statistics1.getCount(), 0);
        assertEquals(3.0, statistics1.getMax(), 0);
        assertEquals(2.0, statistics1.getMin(), 0);

        Statistics statistics3 = new Statistics();
        statistics3.setSum(2.0);
        statistics3.setCount(2L);
        statistics3.setMax(1.0);
        statistics3.setMin(1.0);

        statistics1.merge(statistics3);

        assertEquals(4.0, statistics1.getSum(), 0);
        assertEquals(4L, statistics1.getCount(), 0);
        assertEquals(3.0, statistics1.getMax(), 0);
        assertEquals(1.0, statistics1.getMin(), 0);
    }

    @Test
    public void testEmptyUpdate() {
        Statistics statistics1 = new Statistics();
        statistics1.update(new Transaction(1.0, 111_111L));

        assertEquals(1.0, statistics1.getSum(), 0);
        assertEquals(1L, statistics1.getCount(), 0);
        assertEquals(1.0, statistics1.getMax(), 0);
        assertEquals(1.0, statistics1.getMin(), 0);
    }

    @Test
    public void testUpdate() {
        Statistics statistics1 = new Statistics();
        statistics1.setSum(1.0);
        statistics1.setCount(1L);
        statistics1.setMax(2.0);
        statistics1.setMin(2.0);

        statistics1.update(new Transaction(3.0, 111_111L));

        assertEquals(4.0, statistics1.getSum(), 0);
        assertEquals(2L, statistics1.getCount(), 0);
        assertEquals(3.0, statistics1.getMax(), 0);
        assertEquals(2.0, statistics1.getMin(), 0);


        statistics1.update(new Transaction(1.0, 111_111L));

        assertEquals(5.0, statistics1.getSum(), 0);
        assertEquals(3L, statistics1.getCount(), 0);
        assertEquals(3.0, statistics1.getMax(), 0);
        assertEquals(1.0, statistics1.getMin(), 0);

        statistics1.update(new Transaction(2.0, 111_111L));

        assertEquals(7.0, statistics1.getSum(), 0);
        assertEquals(4L, statistics1.getCount(), 0);
        assertEquals(3.0, statistics1.getMax(), 0);
        assertEquals(1.0, statistics1.getMin(), 0);
    }
}
