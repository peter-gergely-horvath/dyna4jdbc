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


package com.github.dyna4jdbc.samples;

import java.sql.*;



public class EchoParameterDemo {

    private static final String JAVASCRIPT_SCRIPT = ""
            + "	print('Message::');         \n"
            + "	print(parameter1);          \n"
            + "	print(parameter2);          \n";


    public static void main(String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:dyna4jdbc:scriptengine:JavaScript")) {

            try (PreparedStatement preparedStatement =
                         connection.prepareStatement(JAVASCRIPT_SCRIPT)) {

                preparedStatement.setString(1, "Hello");
                preparedStatement.setString(2, "World");

                ResultSet resultSet = preparedStatement.executeQuery();
                printResultSetWithHeaders(resultSet);
            }
        }
    }

    private static void printResultSetWithHeaders(ResultSet rs)
            throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        final int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnLabel = metaData.getColumnLabel(i);
            int columnDisplaySize = metaData.getColumnDisplaySize(i);
            String formatStr = "%" + columnDisplaySize + "s | ";
            System.out.format(formatStr, columnLabel);
        }
        System.out.println();

        for (int i = 1; i <= columnCount; i++) {
            int columnDisplaySize = metaData.getColumnDisplaySize(i);
            String formatStr = "%" + columnDisplaySize + "s | ";
            System.out.format(String.format(formatStr, "").replace(' ', '-'));
        }
        System.out.println();

        while (rs.next()) {

            for (int i = 1; i <= columnCount; i++) {
                int columnDisplaySize = metaData.getColumnDisplaySize(i);
                String formatStr = "%" + columnDisplaySize + "s | ";
                System.out.format(formatStr, rs.getString(i));
            }
            System.out.println();
        }
    }
}
