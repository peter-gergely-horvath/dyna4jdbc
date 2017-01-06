/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandler;
import com.github.dyna4jdbc.internal.common.util.collection.BoundedIterator;

/**
 * @author Peter G. Horvath
 */
public abstract class DataRowListResultSet<T> extends ColumnHandlerResultSet<T> {

    private static final int SQL_INDEX_OFFSET = 1;
    private static final int SQL_INDEX_FIRST_ROW = 1;

    private T currentRow = null;
    private int javaIndex = -1;


    private final GuardedResultSetState resultSetState = new GuardedResultSetState();

    private final Iterator<T> rowIterator;

    public DataRowListResultSet(
            List<T> dataRows, Statement statement, List<ColumnHandler> columnHandlers)  {
        super(statement, columnHandlers);

        this.rowIterator = RowIteratorFactory.getInstance().getRowIterator(dataRows, statement);
    }

    @Override
    public final int getRow() throws SQLException {
        return javaIndex + SQL_INDEX_OFFSET;
    }

    public final boolean next() throws SQLException {
        checkNotClosed();

        GuardedResultSetState.State currentState = resultSetState.getCurrentState();
        switch (currentState) {
            case BEFORE_FIRST:
                return handleNextOnBeforeFirst();


            case ITERATING_OVER_RESULTS:
                return handleNextOnIteratingOverResults();


            case AFTER_LAST:
                throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                        "Calling next() in state " + currentState);

            default:
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
                        "Unexpected currentState: " + currentState);
        }
    }

    private boolean handleNextOnBeforeFirst() throws SQLException {
        if (rowIterator.hasNext()) {
            resultSetState.transitionTo(GuardedResultSetState.State.ITERATING_OVER_RESULTS);
            ++javaIndex;
            currentRow = rowIterator.next();
        } else {
            resultSetState.transitionTo(GuardedResultSetState.State.AFTER_LAST);
        }

        return resultSetState.isInState(GuardedResultSetState.State.ITERATING_OVER_RESULTS);
    }

    private boolean handleNextOnIteratingOverResults() throws SQLException {
        if (rowIterator.hasNext()) {
            currentRow = rowIterator.next();
            ++javaIndex;
        } else {
            resultSetState.transitionTo(GuardedResultSetState.State.AFTER_LAST);
        }

        return resultSetState.isInState(GuardedResultSetState.State.ITERATING_OVER_RESULTS);
    }



    protected final T getCurrentRow() throws SQLException {
        if (currentRow == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
                    "currentRow is null in state: " + resultSetState);
        }

        return currentRow;
    }

    protected final void skipNextRowIfPresent() {
        Iterator<T> theActualRowIterator;

        if (this.rowIterator instanceof BoundedIterator) {
            theActualRowIterator = ((BoundedIterator<T>) this.rowIterator).getDelegate();
        } else {
            theActualRowIterator = this.rowIterator;
        }


        if (theActualRowIterator.hasNext()) {
            theActualRowIterator.next();
        }
    }

    @Override
    public final void refreshRow() throws SQLException {
        // We implement result set type TYPE_FORWARD_ONLY
        // where the support of this method is optional
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Updating ResultSet data");
    }

    @Override
    public final boolean isBeforeFirst() throws SQLException {
        return resultSetState.isInState(GuardedResultSetState.State.BEFORE_FIRST);
    }

    @Override
    public final boolean isAfterLast() throws SQLException {
        return resultSetState.isInState(GuardedResultSetState.State.AFTER_LAST);
    }

    @Override
    public final boolean isFirst() throws SQLException {
        return getRow() == SQL_INDEX_FIRST_ROW;
    }

    @Override
    public final boolean isLast() throws SQLException {
        return resultSetState.isInState(GuardedResultSetState.State.ITERATING_OVER_RESULTS) && !rowIterator.hasNext();
    }

    @Override
    public final void beforeFirst() throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#beforeFirst()");
    }

    @Override
    public final void afterLast() throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#afterLast()");
    }

    @Override
    public final boolean first() throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#first()");
    }

    @Override
    public final boolean last() throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#last()");
    }

    @Override
    public final boolean absolute(int row) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "Moving cursor by absolute(int)");
    }

    @Override
    public final boolean relative(int rows) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "Moving cursor by relative(int)");
    }

    @Override
    public final boolean previous() throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "Moving cursor by previous()");
    }

    @Override
    public final void setFetchDirection(int direction) throws SQLException {
        if (direction != ResultSet.FETCH_FORWARD
                && direction != ResultSet.FETCH_REVERSE
                && direction != ResultSet.FETCH_UNKNOWN) {

            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "Invalid direction:" + direction);
        }

        if (direction != ResultSet.FETCH_FORWARD) {
            throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                    "Only FETCH_FORWARD fetch direction is supported: " + direction);
        }
    }

    @Override
    public final int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public final int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }


}
