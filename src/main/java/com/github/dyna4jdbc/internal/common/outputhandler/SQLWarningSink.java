package com.github.dyna4jdbc.internal.common.outputhandler;

import java.sql.SQLWarning;

/**
 * A component, which consumes {@code SQLWarning}s.
 *
 * @author Peter Horvath
 */
public interface SQLWarningSink {

    void onSQLWarning(SQLWarning warning);
}
