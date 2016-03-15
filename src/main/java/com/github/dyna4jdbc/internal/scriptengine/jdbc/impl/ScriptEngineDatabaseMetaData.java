package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.DynaDriver;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractDatabaseMetaData;

import java.sql.Connection;
import java.sql.SQLException;

class ScriptEngineDatabaseMetaData extends AbstractDatabaseMetaData {

    private final ScriptEngineConnection scriptEngineConnection;

    ScriptEngineDatabaseMetaData(ScriptEngineConnection scriptEngineConnection) {
        this.scriptEngineConnection = scriptEngineConnection;
    }

    public Connection getConnection() throws SQLException {
        return scriptEngineConnection;
    }

    public String getDriverName() throws SQLException {
        return DynaDriver.DRIVER_NAME;
    }

    public String getDriverVersion() throws SQLException {
        return DynaDriver.DRIVER_VERSION;
    }

    public String getDatabaseProductName() throws SQLException {
        return scriptEngineConnection.getEngineDescription();
    }

    public String getDatabaseProductVersion() throws SQLException {
        return scriptEngineConnection.getEngineVersion();
    }

    public int getDriverMajorVersion() {
        return DynaDriver.DRIVER_VERSION_MAJOR;
    }

    public int getDriverMinorVersion() {
        return DynaDriver.DRIVER_VERSION_MINOR;
    }
}
