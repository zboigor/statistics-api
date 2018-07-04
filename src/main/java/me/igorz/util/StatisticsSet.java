package me.igorz.util;


import me.igorz.model.Statistics;
import me.igorz.model.Transaction;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Set (actually tree) for storing aggregated statistics for 1 second.
 * It is required for fast getting statistics by the last second.
 * It is sorted by transactions millisecond time.
 * Statistics is aggregated from max milliseconds to min,
 * e.g. we have 5 transactions with values 2.0
 * <p>
 * count   sum   max   min
 * 115   5       10    ...   ...
 * 276   4       8
 * 485   3       6
 * 692   2       4
 * 856   1       2
 * <p>
 * So if we need data by all milliseconds greater than 500, we simply take data from 692 and so on
 */
public class StatisticsSet {

    private NavigableSet<Statistics> set;

    public StatisticsSet() {
        set = new TreeSet<>(Comparator.comparingInt(Statistics::getTime));
    }

    /**
     * Insert transaction value.
     * <p>
     * Updates all subtree that is lower or equal than transaction milliseconds.
     * If the same value exists, they are merged.
     *
     * @param transaction Transaction to insert
     */
    public void addTransaction(Transaction transaction) {
        Statistics st = new Statistics();
        st.update(transaction);
        st.setTime(Utils.getMilliseconds(transaction.getTimestamp()));

        // update all lower
        set.headSet(st, false)
                .forEach(statistics -> statistics.update(transaction));

        // greater or equal
        Statistics ceiling = set.ceiling(st);

        if (ceiling != null) {
            if (ceiling.getTime().equals(st.getTime())) {
                ceiling.merge(st);
            } else {
                st.merge(ceiling);
                set.add(st);
            }
        } else {
            set.add(st);
        }
    }

    /**
     * Return statistics by timestamp.
     * <p>
     * Operates in O(log(1000)) which is actually O(1)
     */
    public Statistics getStatistics(Long timestamp) {
        Statistics statistics = new Statistics();
        statistics.setTime(Utils.getMilliseconds(timestamp));
        // greater or equal
        Statistics ceiling = set.ceiling(statistics);
        if (ceiling != null) {
            return ceiling;
        } else {
            return statistics;
        }
    }
}
