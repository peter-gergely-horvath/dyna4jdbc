package com.github.dyna4jdbc.internal;

import java.sql.SQLException;

public class ClosableSQLObject {

    private boolean closed = false;

    protected void checkNotClosed() throws SQLException {
        checkNotClosed(this.toString());
    }

    protected final void checkNotClosed(String message) throws SQLException {
        if(closed) {
            throw SQLError.OBJECT_CLOSED.raiseException(message);
        }
    }


    public void close() throws SQLException {
        closed = true;
    }

    public final boolean isClosed() throws SQLException {
        return closed;
    }
}
