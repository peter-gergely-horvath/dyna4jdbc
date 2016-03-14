package com.github.dyna4jdbc;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.scriptengine.ResultSetObjectIterable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestScriptEngine {

    @Test
    public void testDriver() throws Exception {

        Class.forName("com.github.dyna4jdbc.DynaDriver");

        Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:JavaScript");

        Statement statement = connection.createStatement();

        statement.executeUpdate("var msg = 'Hello World\tI am here!'");

        String script = "print('Foo\tBar') ; print(msg)";

        boolean results = statement.execute(script);
        int rsCount = 0;

        //Loop through the available result sets.
        do {
            if (results) {
                ResultSet rs = statement.getResultSet();
                rsCount++;

                //Show data from the result set.
                System.out.println("RESULT SET #" + rsCount);
                while (rs.next()) {
                    System.out.format("%s %s %n",
                            rs.getString(1), rs.getString(2));
                }
                rs.close();
            }

            results = statement.getMoreResults();
        } while (results);
        statement.close();
    }
}
