package com.github.dyna4jdbc;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.connection.scriptengine.ResultSetObjectIterable;

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
        statement.executeUpdate("print (msg)");

        statement.execute("print ('Hello');print (msg)");

        ResultSet resultSet = statement.getResultSet();
        
        Assert.assertTrue(resultSet.isWrapperFor(ResultSetObjectIterable.class));
        
        System.out.println("--- BEGIN: Iterator ---");
        for(Object obj : resultSet.unwrap(ResultSetObjectIterable.class)) {
        	System.out.println(obj);
        }
        System.out.println("--- END: Iterator ---");
        
        System.out.println("--- BEGIN: ResultSet ---");
        while (resultSet.next()) {
            String str = resultSet.getString(1);
			System.out.println(str);
        }
        System.out.println("--- END: ResultSet ---");

    }

}
