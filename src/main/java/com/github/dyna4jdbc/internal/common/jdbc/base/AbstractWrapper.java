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

import java.sql.SQLException;
import java.sql.Wrapper;

import com.github.dyna4jdbc.internal.JDBCError;

/**
 * Abstract base class for classes which have to implement {@code java.sql.Wrapper}
 * interface. This implementation provides common, centralized handling of cases,
 * where unwrapping is not required. For any class, where unwrapping is required
 * for a specific interface, the subclass should implement a logic for the specific
 * type and delegate the call to the super class in case a different type is requested
 * so as to trigger the default error handling logic.
 */
public abstract class AbstractWrapper implements Wrapper {

    //CHECKSTYLE.OFF: DesignForExtension : incorrect detection of "is not designed for extension"
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw JDBCError.CANNOT_UNWARP_OBJECT.raiseSQLException(iface, this.getClass());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
    //CHECKSTYLE.ON: DesignForExtension
}
