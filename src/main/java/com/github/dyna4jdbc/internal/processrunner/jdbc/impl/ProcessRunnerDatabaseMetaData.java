package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.sql.SQLException;

import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractDatabaseMetaData;

public class ProcessRunnerDatabaseMetaData extends AbstractDatabaseMetaData<ProcessRunnerConnection> {

    ProcessRunnerDatabaseMetaData(ProcessRunnerConnection scriptEngineConnection) {
        super(scriptEngineConnection);
    }

    public String getDatabaseProductName() throws SQLException {
        return getConnection().getProductName();
    }

    public String getDatabaseProductVersion() throws SQLException {
        return getConnection().getProductVersion();
    }
}
