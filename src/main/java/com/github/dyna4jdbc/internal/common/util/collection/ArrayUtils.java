package com.github.dyna4jdbc.internal.common.util.collection;

/**
 * @author Peter Horvath
 */
public final class ArrayUtils {

    private ArrayUtils() {
        // static utility class
    }

    public static <T> T tryGetByIndex(T[] array, int index) {
        if (array == null) {
            throw new NullPointerException("argument array cannot be null");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }

        T result;
        if (array.length > index) {
            result = array[index];
        } else {
            result = null;
        }
        return result;
    }


}
