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

 
package com.github.dyna4jdbc.internal;

/**
 * <p>
 * Thrown to indicate that the operation failed and
 * a {@code SQLException} is to be thrown to the
 * JDBC API caller.</p>
 *
 * <p>
 * This class should be used in cases, where
 * the method signature does not allow a
 * {@code SQLException} to be thrown: higher
 * layers of the code must catch this exception
 * and wrap it into {@code SQLException}, with
 * the message and SQLState taken from this
 * exception.
 * </p>
 *
 * @see RuntimeDyna4JdbcException#getMessage()
 * @see RuntimeDyna4JdbcException#getSqlState()
 *
 */
public class RuntimeDyna4JdbcException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String sqlState;

    public RuntimeDyna4JdbcException(String message, String sqlState) {
        super(message);
        this.sqlState = sqlState;
    }

    public RuntimeDyna4JdbcException(String message, Throwable cause, String sqlState) {
        super(message, cause);
        this.sqlState = sqlState;
    }

    public final String getSqlState() {
        return sqlState;
    }

}
