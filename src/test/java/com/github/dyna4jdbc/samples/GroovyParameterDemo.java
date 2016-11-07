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

public class GroovyParameterDemo {

    private static final String GROOVY_SCRIPT = ""
            + "	import groovy.json.JsonSlurper														\n"
            + "																						\n"
            + " def printRow(String... values) { println values.join(\"\t\") }						\n"
            + "																						\n"
            + "	def exchange = parameter1; // parameter1 is bound via the JDBC API!                 \n"
            + "	def tickers = parameter2; // parameter2 is bound via the JDBC API!                  \n"
            + "																						\n"
            + " def jsonData = new URL(\"http://www.google.com/finance/info?"
                + "infotype=infoquoteall&q=${exchange}:${tickers}\").text.replaceFirst('//', '')	\n"
            + " def data = new JsonSlurper().parseText(jsonData)									\n"
            + " printRow 'Ticker::', 'Name::', 'Open::', 'Close::', 'Change::'						\n"
            + " data.each { printRow it['t'], it['name'], it['op'], it['l_cur'], it['c'] } 			\n";

    public static void main(String[] args) throws SQLException {

        try (Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:groovy")) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(GROOVY_SCRIPT)) {


                preparedStatement.setString(1, "NASDAQ"); // setting variable "parameter1"
                preparedStatement.setString(2, "AAPL,IBM,MSFT,GOOG"); // setting variable "parameter2"


                boolean results = preparedStatement.execute();
                while (results) {
                    try (ResultSet rs = preparedStatement.getResultSet()) {

                        printResultSetWithHeaders(rs);
                    }

                    results = preparedStatement.getMoreResults();
                }
            }
        }
    }

    private static void printResultSetWithHeaders(ResultSet rs) throws SQLException {
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

