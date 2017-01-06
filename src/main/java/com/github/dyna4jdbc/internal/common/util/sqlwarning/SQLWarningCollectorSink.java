package com.github.dyna4jdbc.internal.common.util.sqlwarning;

import java.sql.SQLWarning;
import java.util.LinkedList;

import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;

public final class SQLWarningCollectorSink implements SQLWarningSink {

    private final LinkedList<SQLWarning> list = new LinkedList<>();

    @Override
    public void onSQLWarning(SQLWarning warning) {
        list.add(warning);
    }

    public LinkedList<SQLWarning> getWarnings() {
        return list; // defensive copy?
    }
}
