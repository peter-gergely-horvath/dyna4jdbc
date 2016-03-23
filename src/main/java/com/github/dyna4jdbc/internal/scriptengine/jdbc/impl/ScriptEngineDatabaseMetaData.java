package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import java.sql.SQLException;

import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractDatabaseMetaData;

public class ScriptEngineDatabaseMetaData extends AbstractDatabaseMetaData<ScriptEngineConnection> {

    ScriptEngineDatabaseMetaData(ScriptEngineConnection scriptEngineConnection) {
        super(scriptEngineConnection);
    }

    public String getDatabaseProductName() throws SQLException {
        return getConnection().getEngineDescription();
    }

    public String getDatabaseProductVersion() throws SQLException {
        return getConnection().getEngineVersion();
    }
}
