package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import java.sql.RowId;

public class ScriptEngineRowId<T> implements RowId {

    private final String name;
    private final T qualifier;

    ScriptEngineRowId(String name, T qualifier) {
        if(name == null || "".equals(name)) {
            throw new RuntimeException("name cannot be null");
        }

        this.name = name;
        this.qualifier = qualifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScriptEngineRowId that = (ScriptEngineRowId) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null);

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
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        return result;
    }
}
