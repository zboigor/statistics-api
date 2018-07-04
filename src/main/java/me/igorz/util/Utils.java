package me.igorz.util;

public class Utils {

    public static long dropMilliseconds(long timestamp) {
        return timestamp / 1000 * 1000;
    }

    public static int getMilliseconds(long timestamp) {
        return (int) (timestamp - dropMilliseconds(timestamp));
    }
}
