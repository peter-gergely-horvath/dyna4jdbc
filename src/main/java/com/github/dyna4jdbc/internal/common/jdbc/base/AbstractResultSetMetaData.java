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
package com.github.dyna4jdbc.internal.common.jdbc.base;


import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.github.dyna4jdbc.internal.JDBCError;

public abstract class AbstractResultSetMetaData extends AbstractWrapper implements ResultSetMetaData {

    @Override
    public final boolean isAutoIncrement(int column) throws SQLException {
        // A column built from script output is NOT AutoIncrement.
        return false;
    }

    @Override
    public final boolean isCaseSensitive(int column) throws SQLException {
        // We allow a result set to have two distinct columns
        // "foo" and "FOO": hence, columns ARE CaseSensitive.
        return true;
    }

    @Override
    public final boolean isSearchable(int column) throws SQLException {
        // We know nothing about the columns: hence we
        // report all as NON-Searchable.
        return false;
    }

    @Override
    public final String getSchemaName(int column) throws SQLException {
        // Javadoc: "schema name or "" if not applicable"

        // Schema is not applicable for a column built
        // from script output: hence we report "".

        return "";
    }

    @Override
    public final String getTableName(int column) throws SQLException {
        // Javadoc: "table name or "" if not applicable"

        // Table is not applicable for a column built
        // from script output: hence we report "".

        return "";
    }

    @Override
    public final String getCatalogName(int column) throws SQLException {
        // Javadoc: "the name of the catalog for the table in which
        // the given column appears or "" if not applicable"

        // Catalog is not applicable for a column built
        // from script output: hence we report "".

        return "";
    }

    @Override
    public final boolean isReadOnly(int column) throws SQLException {
        // A column built from script output cannot
        // be written, hence we report it as read-only
        return true;
    }

    @Override
    public final boolean isWritable(int column) throws SQLException {
        // A column built from script output cannot
        // be written, hence we report it as read-only
        return false;
    }

    @Override
    public final boolean isDefinitelyWritable(int column) throws SQLException {
        // A column built from script output cannot
        // be written, hence we report it as read-only
        return false;
    }
}
