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

 
package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.base.RowListResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public final class EmptyResultSet extends RowListResultSet<List<String>> {

    public EmptyResultSet() {
        super(Collections.emptyList(), null, Collections.emptyList());
    }

    @Override
    protected String getRawCellValueBySqlColumnIndex(int sqlIndex) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
    }

    @Override
    public String getCursorName() throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Result set is empty!");
    }
}
