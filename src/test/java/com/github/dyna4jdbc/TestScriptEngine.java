package com.github.dyna4jdbc;

import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestScriptEngine {

    @Test
    public void testDriver() throws Exception {

        Class.forName("com.github.dyna4jdbc.DynaDriver");

        Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:JavaScript");

        System.out.println(connection);

        Statement statement = connection.createStatement();

        statement.executeUpdate("var myNumber = 0.5 ");
        statement.executeUpdate("var msg = 'Hello World'");

        boolean execute = statement.execute("print ('Hello')");

        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()) {
            System.out.println(resultSet.getObject(1));
        }

    }

}
