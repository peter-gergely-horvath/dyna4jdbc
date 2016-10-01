package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.ResultSetScriptOutputHandlerConstants;
import com.github.dyna4jdbc.internal.common.util.collection.BoundedIterator;

final class RowIteratorFactory {
    
    private RowIteratorFactory() {
        // no external instances
    }
    
    private static final RowIteratorFactory INSTANCE = new RowIteratorFactory();
    
    static RowIteratorFactory getInstance() {
        return INSTANCE;
    }
    

    <T> Iterator<T> getRowIterator(List<T> dataRows, Statement statement) {
        try {
            int maxRows;

            if (statement != null) {
                maxRows = statement.getMaxRows();
            } else {
                maxRows = ResultSetScriptOutputHandlerConstants.MAX_ROWS_NO_BOUNDS;
            }

            if (maxRows != ResultSetScriptOutputHandlerConstants.MAX_ROWS_NO_BOUNDS) {
                return new BoundedIterator<>(dataRows.iterator(), maxRows);
            } else {
                return dataRows.iterator();
            }

        } catch (SQLException e) {
            // should not happen, since the statement is active when
            // we create this object
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    e, "Caught SQLException");
        }
    }
}
