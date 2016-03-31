package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.sql.SQLException;

import com.github.dyna4jdbc.internal.SQLError;

public class BasicSQLObject extends AbstractWrapper {

    private boolean closed = false;

    protected void checkNotClosed() throws SQLException {
        checkNotClosed(this.toString());
    }

    protected final void checkNotClosed(String objectIdentifier) throws SQLException {
        if(closed) {
            throw SQLError.OBJECT_CLOSED.raiseSQLException(objectIdentifier);
        }
    }


    public void close() throws SQLException {
        closed = true;
    }

    public final boolean isClosed() throws SQLException {
        return closed;
    }
	
}
