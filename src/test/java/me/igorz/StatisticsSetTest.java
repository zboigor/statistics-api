package me.igorz;

import me.igorz.model.Statistics;
import me.igorz.model.Transaction;
import me.igorz.util.StatisticsSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatisticsSetTest {

    @Test
    public void testAddTransaction() {
        StatisticsSet set = new StatisticsSet();
        set.addTransaction(new Transaction(10.0, 10L));

        Statistics statistics = set.getStatistics(0L);

        assertEquals(1, statistics.getCount(), 0);
        assertEquals(10, statistics.getSum(), 0);
        assertEquals(10, statistics.getMin(), 0);
        assertEquals(10, statistics.getMax(), 0);

        set.addTransaction(new Transaction(8.0, 50L));

        statistics = set.getStatistics(0L);

        assertEquals(2, statistics.getCount(), 0);
        assertEquals(18, statistics.getSum(), 0);
        assertEquals(8, statistics.getMin(), 0);
        assertEquals(10, statistics.getMax(), 0);

        statistics = set.getStatistics(30L);

        assertEquals(1, statistics.getCount(), 0);
        assertEquals(8, statistics.getSum(), 0);
        assertEquals(8, statistics.getMin(), 0);
        assertEquals(8, statistics.getMax(), 0);

        set.addTransaction(new Transaction(3.0, 30L));

        statistics = set.getStatistics(0L);

        assertEquals(3, statistics.getCount(), 0);
        assertEquals(21, statistics.getSum(), 0);
        assertEquals(3, statistics.getMin(), 0);
        assertEquals(10, statistics.getMax(), 0);

        set.addTransaction(new Transaction(3.0, 30L));

        statistics = set.getStatistics(0L);

        assertEquals(4, statistics.getCount(), 0);
        assertEquals(24, statistics.getSum(), 0);
        assertEquals(3, statistics.getMin(), 0);
        assertEquals(10, statistics.getMax(), 0);

        statistics = set.getStatistics(40L);

        assertEquals(1, statistics.getCount(), 0);
        assertEquals(8, statistics.getSum(), 0);
        assertEquals(8, statistics.getMin(), 0);
        assertEquals(8, statistics.getMax(), 0);

        statistics = set.getStatistics(100L);

        assertEquals(0, statistics.getCount(), 0);
        assertEquals(0, statistics.getSum(), 0);
        assertEquals(Double.MAX_VALUE, statistics.getMin(), 0);
        assertEquals(Double.MIN_VALUE, statistics.getMax(), 0);
    }
}
