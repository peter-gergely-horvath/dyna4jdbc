package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.sql.SQLException;
import java.sql.Wrapper;

import com.github.dyna4jdbc.internal.SQLError;

public class AbstractWrapper implements Wrapper {
	
	@Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw SQLError.CANNOT_UNWARP_OBJECT.raiseException(iface, this.getClass());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

}
