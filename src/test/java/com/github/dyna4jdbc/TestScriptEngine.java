package com.github.dyna4jdbc;

import org.testng.Assert;
import org.testng.annotations.Test;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class TestScriptEngine {

    @Test
    public void testDriver() throws Exception {

        Class.forName("com.github.dyna4jdbc.DynaDriver");

        Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:JavaScript");

        Statement statement = connection.createStatement();

        statement.executeUpdate("var msg = '123\tHello World\tI am here!'");

        String script = "print('42\tFoo\tBar') ; print(msg)";

        boolean results = statement.execute(script);
        int rsCount = 0;

        //Loop through the available result sets.
        do {
            if (results) {
                ResultSet rs = statement.getResultSet();
                rsCount++;

                //Show data from the result set.
                System.out.println("RESULT SET #" + rsCount);
                ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();

                while (rs.next()) {

                    for(int i=1; i<=columnCount; i++ ) {
                    	String columnLabel = metaData.getColumnLabel(i);
                    	System.out.format("%s ", rs.getString(columnLabel));
                    }
                    System.out.println();
                }
                rs.close();
            }

            results = statement.getMoreResults();
        } while (results);
        statement.close();
    }
    
    @Test
    public void testHeaders() throws Exception {

        Class.forName("com.github.dyna4jdbc.DynaDriver");

        Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:JavaScript");

        Statement statement = connection.createStatement();

        String script = "print('A::\tB::') ; print('First A\tFirst B'); print('Second A\tSecond B')";

        boolean results = statement.execute(script);
        int rsCount = 0;

        //Loop through the available result sets.
        do {
            if (results) {
                ResultSet rs = statement.getResultSet();
                rsCount++;

                //Show data from the result set.
                System.out.println("RESULT SET #" + rsCount);
                ResultSetMetaData metaData = rs.getMetaData();
				final int columnCount = metaData.getColumnCount();

            	for(int i=1; i<=columnCount; i++ ) {
                	String columnLabel = metaData.getColumnLabel(i);
                	int columnDisplaySize = metaData.getColumnDisplaySize(i);
                	String formatStr = "%" + columnDisplaySize + "s | ";
                	System.out.format(formatStr, columnLabel);
                }
                System.out.println();
            	
                for(int i=1; i<=columnCount; i++ ) {
                	int columnDisplaySize = metaData.getColumnDisplaySize(i);
                	String formatStr = "%" + columnDisplaySize + "s | ";
                	System.out.format(String.format(formatStr, "").replace(' ', '-'));
                }
                System.out.println();
                
				
                while (rs.next()) {

                    for(int i=1; i<=columnCount; i++ ) {
                    	int columnDisplaySize = metaData.getColumnDisplaySize(i);
                    	String formatStr = "%" + columnDisplaySize + "s | ";
                    	System.out.format(formatStr, rs.getString(i));
                    }
                    System.out.println();
                }
                rs.close();
            }

            results = statement.getMoreResults();
        } while (results);
        statement.close();
    }
}
