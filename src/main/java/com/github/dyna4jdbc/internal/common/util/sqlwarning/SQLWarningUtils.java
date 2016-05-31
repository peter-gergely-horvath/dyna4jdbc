package com.github.dyna4jdbc.internal.common.util.sqlwarning;

import java.sql.SQLWarning;

/**
 * @author Peter Horvath
 */
public final class SQLWarningUtils {

    private SQLWarningUtils() {
        // static utility class -- no instances allowed
    }

    public static SQLWarning chainSQLWarning(
            final SQLWarning current,
            final SQLWarning newWarning) {

        if (current == null) {
            return newWarning;
        }

        SQLWarning last = current;
        while (true) {

            SQLWarning next = last.getNextWarning();
            if (next == null) {
                break;
            }

            last = next;
        }

        last.setNextWarning(newWarning);

        return current;

    }

}
