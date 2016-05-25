package com.github.dyna4jdbc.internal.common.util.collection;

/**
 * @author Peter Horvath
 */
public final class ArrayUtils {

    private ArrayUtils() {
        // static utility class
    }

    public static <T> T tryGetByIndex(T[] array, int index) {
        if (index < 0) {
            throw new IllegalStateException("Invalid index: " + index);
        }

        T result;
        if (array.length >= index + 1) {
            result = array[index];
        } else {
            result = null;
        }
        return result;
    }


}
