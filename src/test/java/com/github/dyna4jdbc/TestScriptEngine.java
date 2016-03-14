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

        String databaseProductName = connection.getMetaData().getDatabaseProductName();
        String databaseProductVer = connection.getMetaData().getDatabaseProductVersion();

        Statement statement = connection.createStatement();

        statement.executeUpdate("var myNumber = 0.5 ");
        statement.executeUpdate("var msg = 'Hello World\tI am here!'");

        statement.execute("print('Foo\tBar') ; print(msg)");

        ResultSet resultSet = statement.getResultSet();
        
        System.out.println("--- BEGIN: ResultSet ---");
        while (resultSet.next()) {
            String str1 = resultSet.getString(1);
            String str2 = resultSet.getString(2);

			System.out.format("%s %s %n", str1, str2);
        }
        System.out.println("--- END: ResultSet ---");

        resultSet.close();
    }

}
