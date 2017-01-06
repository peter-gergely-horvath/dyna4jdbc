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

import java.sql.Connection;
import java.sql.SQLException;

import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractDatabaseMetaData;

public final class GenericDatabaseMetaData extends AbstractDatabaseMetaData<Connection> {

    private String databaseProductName;
    private String databaseProductVersion;

    public GenericDatabaseMetaData(Connection connection,
            String databaseProductName, String databaseProductVersion) {

        super(connection);
        this.databaseProductName = databaseProductName;
        this.databaseProductVersion = databaseProductVersion;
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        return databaseProductName;
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return databaseProductVersion;
    }
}
