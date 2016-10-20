/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
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

 
package com.github.dyna4jdbc.internal.common.jdbc.generic;

import java.sql.SQLException;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractWrapper;

final class EmptyParameterMetaData extends AbstractWrapper implements java.sql.ParameterMetaData {

    private static final int ZERO_PARAMETER_COUNT = 0;
    private static final String PARAMETER_METADATA_NOT_AVAILABLE = "Parameter meta data is not available";

    @Override
    public int getParameterCount() throws SQLException {
        return ZERO_PARAMETER_COUNT;
    }

    @Override
    public int isNullable(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public boolean isSigned(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public int getPrecision(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public int getScale(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public int getParameterType(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public String getParameterTypeName(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public String getParameterClassName(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public int getParameterMode(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

}
