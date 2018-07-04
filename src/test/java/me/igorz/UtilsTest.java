package me.igorz;

import me.igorz.util.Utils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void testDropMilliseconds() {
        long ts = 1_000_000L;
        assertEquals(ts, Utils.dropMilliseconds(ts));

        long ts2 = 1_000_666L;
        assertEquals(ts, Utils.dropMilliseconds(ts2));
    }

    @Test
    public void testGetMilliseconds() {
        long ts = 1_000_000L;
        assertEquals(0, Utils.getMilliseconds(ts));

        long ts2 = 1_000_666L;
        assertEquals(666, Utils.getMilliseconds(ts2));
    }
}
