package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.base.DataRowListResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public final class EmptyResultSet extends DataRowListResultSet<List<String>> {

    public EmptyResultSet() {
        super(Collections.emptyList(), null, Collections.emptyList());
    }

    @Override
    protected String getRawCellValueBySqlColumnIndex(int sqlIndex) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
    }

    @Override
    public String getCursorName() throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
    }
}
