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

public class HelloWorldSample {

    public static void main(String[] args) throws SQLException {

        String url = "jdbc:dyna4jdbc:scriptengine:JavaScript";

        try (Connection connection = DriverManager.getConnection(url)) {

            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate("  var message = 'Hello World';  ");
                ResultSet resultSet = statement.executeQuery("  print(message);  ");

                while (resultSet.next()) {
                    String string = resultSet.getString(1);

                    System.out.println(string);
                }
            }
        }
    }
}
