package com.github.dyna4jdbc.internal.connection.scriptengine;

import java.sql.RowId;

public class ScriptEngineRowId implements RowId {

    private final String name;
    private final ScriptEngineStatement scriptEngineStatement;

    ScriptEngineRowId(String name, ScriptEngineStatement scriptEngineStatement) {
        if(name == null || "".equals(name)) {
            throw new RuntimeException("name cannot be null");
        }

        this.name = name;
        this.scriptEngineStatement = scriptEngineStatement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScriptEngineRowId that = (ScriptEngineRowId) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(scriptEngineStatement != null ? !scriptEngineStatement.equals(that.scriptEngineStatement) : that.scriptEngineStatement != null);

    }

    public byte[] getBytes() {
        return name.getBytes();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (scriptEngineStatement != null ? scriptEngineStatement.hashCode() : 0);
        return result;
    }
}
