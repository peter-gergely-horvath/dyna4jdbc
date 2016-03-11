package com.github.dyna4jdbc.internal;

import java.sql.SQLException;

public class ClosableSQLObject {

    private boolean closed = false;

    protected void checkNotClosed() throws SQLException {
        if(closed) {
            throw new SQLException(this + " is closed already");
        }
    }


    public void close() throws SQLException {
        closed = true;
    }

    public boolean isClosed() throws SQLException {
        return closed;
    }
}
